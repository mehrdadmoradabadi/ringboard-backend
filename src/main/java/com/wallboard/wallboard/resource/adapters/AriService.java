package com.wallboard.wallboard.resource.adapters;

import ch.loway.oss.ari4java.ARI;
import ch.loway.oss.ari4java.AriVersion;
import ch.loway.oss.ari4java.generated.models.Channel;
import ch.loway.oss.ari4java.generated.models.Endpoint;
import ch.loway.oss.ari4java.tools.ARIException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallboard.wallboard.pbx.PBX;
import com.wallboard.wallboard.pbx.PBXService;
import org.asteriskjava.live.AsteriskQueue;
import org.asteriskjava.live.DefaultAsteriskServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class AriService {
    @Autowired
    private PBXService pbxService;
    private final ConcurrentMap<String, Thread> pbxThreads = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, DefaultAsteriskServer> serverConnections = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    public List<QueueInfo> getQueues(String pbxId) {
        try {
            PBX pbx = pbxService.findById(pbxId);
            if (pbx == null) {
                throw new RuntimeException("PBX not found with ID: " + pbxId);
            }
            DefaultAsteriskServer asteriskServer = getServerConnection(pbxId, pbx.getHost(),
                    pbx.getUsername(), pbx.getPassword());

            List<QueueInfo> queues = new ArrayList<>();
            for (AsteriskQueue queue : asteriskServer.getQueues()) {
                QueueInfo queueInfo = new QueueInfo();
                queueInfo.setMemberCount(queue.getMembers().size());
                queueInfo.setName(queue.getName());
                queueInfo.setAbandoned(queue.getAbandoned());
                queueInfo.setWaiting(queue.getWaiting());
                queueInfo.setCompleted(queue.getCompleted());
                queues.add(queueInfo);
            }
            return queues;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch queue data: " + e.getMessage(), e);
        }
    }

    public List<AgentInfo> getAgents(String pbxId) throws ARIException {
        PBX pbx = pbxService.findById(pbxId);
        if (pbx == null) {
            throw new RuntimeException("PBX not found with ID: " + pbxId);
        }
        String Host = "http://" + pbx.getHost()+":"+pbx.getPort();
        ARI ari = ARI.build(Host, pbx.getAppName(), pbx.getUsername(), pbx.getPassword(), AriVersion.IM_FEELING_LUCKY);
        List<AgentInfo> agentInfo = new ArrayList<>();
        for (Channel channel : ari.channels().list().execute()) {
            if (isAgentChannel(channel)) {
                AgentInfo agent = new AgentInfo();
                agent.setName(channel.getCaller().getName());
                agent.setStatus(channel.getState());
                agentInfo.add(agent);
            }
        }
        return agentInfo;
    }

    private boolean isAgentChannel(Channel channel) {
        return channel.getCaller() != null &&
                channel.getCaller().getName() != null &&
                channel.getCaller().getName().startsWith("Agent/");
    }

    public List<ExtensionInfo> getExtensions(String pbxId) throws ARIException, RuntimeException {
        PBX pbx = pbxService.findById(pbxId);
        if (pbx == null) {
            throw new RuntimeException("PBX not found with ID: " + pbxId);
        }
        String Host = "http://" + pbx.getHost()+":"+pbx.getPort();
        ARI ari = ARI.build(Host, pbx.getAppName(), pbx.getUsername(), pbx.getPassword(), AriVersion.IM_FEELING_LUCKY);
        List<ExtensionInfo> extensionInfos = new ArrayList<>();
        for (Endpoint endpoint : ari.endpoints().list().execute()) {
            if (isSIPEndpoint(endpoint)) {
                ExtensionInfo extension = new ExtensionInfo();
                extension.setNumber(endpoint.getResource());
                extension.setStatus(endpoint.getState());
                extensionInfos.add(extension);
            }
        }
        return extensionInfos;
    }

    private boolean isSIPEndpoint(Endpoint endpoint) {
        return "SIP".equals(endpoint.getTechnology()) ||
                "PJSIP".equals(endpoint.getTechnology());
    }

    public List<TrunkInfo> getTrunks(String pbxId) throws ARIException {
        PBX pbx = pbxService.findById(pbxId);
        if (pbx == null) {
            throw new RuntimeException("PBX not found with ID: " + pbxId);
        }
        String Host = "http://" + pbx.getHost()+":"+pbx.getPort();
        ARI ari = ARI.build(Host, pbx.getAppName(), pbx.getUsername(), pbx.getPassword(), AriVersion.IM_FEELING_LUCKY);
        List<TrunkInfo> trunkInfo = new ArrayList<>();
        for (Endpoint endpoint : ari.endpoints().list().execute()) {
            if (isTrunkEndpoint(endpoint)) {
                TrunkInfo trunk = new TrunkInfo();
                trunk.setName(endpoint.getResource());
                trunk.setStatus(endpoint.getState());
                trunkInfo.add(trunk);
            }
        }
        return trunkInfo;
    }

    private boolean isTrunkEndpoint(Endpoint endpoint) {
        return "SIP".equals(endpoint.getTechnology()) &&
                endpoint.getResource().contains("trunk");
    }

    public Flux<String> streamUpdates(String pbxId, String interval) {
        return Flux.create(emitter -> {
            synchronized (pbxThreads) {
                if (pbxThreads.containsKey(pbxId)) {
                    emitter.error(new IllegalStateException("Stream already active for PBX: " + pbxId));
                    return;
                }

                Thread pbxThread = new Thread(() -> {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            List<QueueInfo> queues = getQueues(pbxId);
                            String jsonData = objectMapper.writeValueAsString(queues);
                            emitter.next(jsonData);
                            Thread.sleep(Integer.parseInt(interval) * 1000L);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (Exception ex) {
                        emitter.error(ex);
                    } finally {
                        cleanupAfterThread(pbxId);
                    }
                }, "PBX-" + pbxId);

                pbxThreads.put(pbxId, pbxThread);
                pbxThread.start();
            }
            emitter.onDispose(() -> stopStreaming(pbxId));
        });
    }

    private DefaultAsteriskServer getServerConnection(String pbxId, String host,
                                                      String username, String password) {
        return serverConnections.computeIfAbsent(pbxId,
                id -> new DefaultAsteriskServer(host, username, password));
    }

    private void stopStreaming(String pbxId) {
        Thread thread = pbxThreads.remove(pbxId);
        if (thread != null) {
            thread.interrupt();
        }
        cleanupServerConnection(pbxId);
    }

    private void cleanupServerConnection(String pbxId) {
        DefaultAsteriskServer server = serverConnections.remove(pbxId);
        if (server != null) {
            server.shutdown();
        }
    }

    private void cleanupAfterThread(String pbxId) {
        pbxThreads.remove(pbxId);
        cleanupServerConnection(pbxId);
    }
}
