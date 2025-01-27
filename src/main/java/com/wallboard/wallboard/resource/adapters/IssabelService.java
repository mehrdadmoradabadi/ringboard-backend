//package com.wallboard.wallboard.pbx.adapters;
//
//import org.asteriskjava.live.AsteriskQueue;
//import org.asteriskjava.live.DefaultAsteriskServer;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.FluxSink;
//
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;
//
//@Service
//public class IssabelService {
//
//    private final ConcurrentMap<String, Thread> pbxThreads = new ConcurrentHashMap<>();
//
//    private final ConcurrentMap<String, FluxSink<String>> activeStreams = new ConcurrentHashMap<>();
//
//    private final ConcurrentMap<String, DefaultAsteriskServer> serverConnections = new ConcurrentHashMap<>();
//
//    public Flux<String> streamUpdates(String pbxId, String host, String username, String password, String interval) {
//        return Flux.<String>create(emitter -> {
//            synchronized (pbxThreads) {
//                if (pbxThreads.containsKey(pbxId)) {
//                    emitter.error(new IllegalStateException("Stream already active for PBX: " + pbxId));
//                    return;
//                }
//
//                activeStreams.put(pbxId, emitter);
//
//                Thread pbxThread = new Thread(() -> {
//                    try {
//                        while (!Thread.currentThread().isInterrupted()) {
//                            String data = getPBXData(pbxId, host, username, password);
//                            emitter.next(data);
//                            Thread.sleep(Integer.parseInt(interval)* 1000L);
//                        }
//                    } catch (InterruptedException e) {
//                        Thread.currentThread().interrupt();
//                    } catch (Exception ex) {
//                        emitter.error(ex);
//                    } finally {
//                        cleanupAfterThread(pbxId);
//                    }
//                }, "PBX-" + pbxId);
//
//                pbxThreads.put(pbxId, pbxThread);
//                pbxThread.start();
//            }
//
//            emitter.onDispose(() -> stopStreaming(pbxId));
//        });
//    }
//
//    private DefaultAsteriskServer getServerConnection(String pbxId, String host, String username, String password) {
//        return serverConnections.computeIfAbsent(pbxId, id -> {
//            System.out.println("Creating server connection for PBX: " + id);
//            return new DefaultAsteriskServer(host, username, password);
//        });
//    }
//
//    private void cleanupServerConnection(String pbxId) {
//        DefaultAsteriskServer server = serverConnections.remove(pbxId);
//        if (server != null) {
//            System.out.println("Disconnecting server for PBX: " + pbxId);
//            server.shutdown();
//        }
//    }
//
//    private String getPBXData(String pbxId, String host, String username, String password) {
//        try {
//            DefaultAsteriskServer asteriskServer = getServerConnection(pbxId, host, username, password);
//            StringBuilder sb = new StringBuilder();
//            for (AsteriskQueue queue : asteriskServer.getQueues()) {
//                sb.append("Queue Name: ").append(queue.getName())
//                        .append(", Calls in Queue: ").append(queue.getEntries().size()).append("\n")
//                        .append("Agents in Queue: ").append(queue.getMembers().size()).append("\n");
//            }
//            return sb.toString();
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to fetch PBX data for " + pbxId + ": " + e.getMessage());
//        }
//    }
//    private void stopStreaming(String pbxId) {
//        Thread thread = pbxThreads.remove(pbxId);
//        if (thread != null) {
//            thread.interrupt();
//        }
//        cleanupServerConnection(pbxId);
//        activeStreams.remove(pbxId);
//        System.out.println("Stopped and cleaned up for PBX: " + pbxId);
//    }
//
//    private void cleanupAfterThread(String pbxId) {
//        pbxThreads.remove(pbxId);
//        activeStreams.remove(pbxId);
//        cleanupServerConnection(pbxId);
//        System.out.println("Cleaned up resources for PBX: " + pbxId);
//    }
//}
