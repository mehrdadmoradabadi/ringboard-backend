package com.RingBoard.wallboard.resource.asterisk;

import ch.loway.oss.ari4java.ARI;
import ch.loway.oss.ari4java.AriVersion;
import ch.loway.oss.ari4java.generated.models.Endpoint;
import ch.loway.oss.ari4java.tools.ARIException;
import com.RingBoard.wallboard.pbx.PBX;
import com.RingBoard.wallboard.pbx.PBXService;
import com.RingBoard.wallboard.utils.ResourceNotFoundException;
import org.asteriskjava.live.AsteriskQueue;
import org.asteriskjava.live.DefaultAsteriskServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class AriService {
    private final PBXService pbxService;
//    private final ConcurrentMap<String, Thread> pbxThreads = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, DefaultAsteriskServer> serverConnections = new ConcurrentHashMap<>();

    @Autowired
    public AriService(@Lazy PBXService pbxService) {
        this.pbxService = pbxService;
    }
    public List<QueueInfo> getQueues(String pbxId) {
        try {
            PBX pbx = pbxService.findById(pbxId);
            if (pbx == null) {
                throw new ResourceNotFoundException( "PBX not found with ID: " + pbxId);
            }
            Integer port = pbx.getAmiPort() != null ? Integer.parseInt(pbx.getAmiPort()) : 5038;
            DefaultAsteriskServer asteriskServer = getServerConnection(pbxId, pbx.getHost(),
                    pbx.getUsername(), pbx.getPassword(), port);

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

    public List<ExtensionInfo> getExtensions(String pbxId) throws ARIException, RuntimeException {
        PBX pbx = pbxService.findById(pbxId);
        if (pbx == null) {
            throw new ResourceNotFoundException("PBX not found with ID: " + pbxId);
        }
        String Host = "http://" + pbx.getHost()+":"+pbx.getAriPort();
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
            throw new ResourceNotFoundException( "PBX not found with ID: " + pbxId);
        }
        String Host = "http://" + pbx.getHost()+":"+pbx.getAriPort();
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


    private DefaultAsteriskServer getServerConnection(String pbxId, String host,
                                                      String username, String password, Integer port) {
        return serverConnections.computeIfAbsent(pbxId,
                id -> new DefaultAsteriskServer(host, port, username, password));
    }
}
