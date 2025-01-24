package com.wallboard.wallboard.pbx.adapters;

import org.asteriskjava.manager.event.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class PbxUpdaterService {

    private final ConcurrentMap<String, Issabel> issabelInstances = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Thread> pbxThreads = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, FluxSink<PBXEvent>> activeStreams = new ConcurrentHashMap<>();

    public Flux<PBXEvent> streamUpdates(String pbxId, String host, String username, String password) {
        return Flux.<PBXEvent>create(emitter -> {
                    synchronized (pbxThreads) {
                        if (pbxThreads.containsKey(pbxId)) {
                            emitter.error(new IllegalStateException("Stream already active for PBX: " + pbxId));
                            return;
                        }

                        activeStreams.put(pbxId, emitter);

                        Issabel issabelInstance = getIssabelInstance(pbxId, host, username, password);

                        Thread pbxThread = new Thread(() -> {
                            try {
                                issabelInstance.connect(); // Connect to Issabel AMI
                                while (!Thread.currentThread().isInterrupted()) {
                                    Thread.sleep(5000); // Keep thread alive, events are processed via callback
                                }
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            } catch (Exception ex) {
                                emitter.error(ex); // Emit any exception
                            } finally {
                                cleanupAfterThread(pbxId);
                            }
                        }, "PBX-" + pbxId);

                        pbxThreads.put(pbxId, pbxThread);
                        pbxThread.start();
                    }

                    emitter.onDispose(() -> stopStreaming(pbxId));
                })
                .doOnCancel(() -> System.out.println("Stream canceled for PBX: " + pbxId))
                .doOnError(error -> System.err.println("Stream error for PBX: " + pbxId + ": " + error.getMessage()));
    }

    public Issabel getIssabelInstance(String pbxId, String host, String username, String password) {
        return issabelInstances.computeIfAbsent(pbxId, id -> new Issabel(host, 5038, username, password, event -> handleAMIEvent(pbxId, event)));
    }

    private void handleAMIEvent(String pbxId, ManagerEvent event) {
        PBXEvent pbxEvent = null;

        if (event instanceof QueueCallerJoinEvent) {
            QueueCallerJoinEvent joinEvent = (QueueCallerJoinEvent) event;
            pbxEvent = new PBXEvent("CALLER_JOIN", joinEvent.getQueue(), joinEvent.getCallerIdNum());
        } else if (event instanceof QueueCallerLeaveEvent) {
            QueueCallerLeaveEvent leaveEvent = (QueueCallerLeaveEvent) event;
            pbxEvent = new PBXEvent("CALLER_LEAVE", leaveEvent.getQueue(), null);
        } else if (event instanceof NewStateEvent) {
            NewStateEvent stateEvent = (NewStateEvent) event;
            pbxEvent = new PBXEvent("STATE_CHANGE", stateEvent.getChannel(), stateEvent.getState());
        }

        if (pbxEvent != null) {
            FluxSink<PBXEvent> emitter = activeStreams.get(pbxId);
            if (emitter != null) {
                emitter.next(pbxEvent);
            }
        }
    }

    private void stopStreaming(String pbxId) {
        Thread thread = pbxThreads.remove(pbxId);
        if (thread != null) {
            thread.interrupt(); // Stop the thread
        }
        Issabel issabelInstance = issabelInstances.remove(pbxId);
        if (issabelInstance != null) {
            issabelInstance.disconnect();
        }
        activeStreams.remove(pbxId);
        System.out.println("Stopped and cleaned up for PBX: " + pbxId);
    }

    private void cleanupAfterThread(String pbxId) {
        pbxThreads.remove(pbxId);
        Issabel issabelInstance = issabelInstances.remove(pbxId);
        if (issabelInstance != null) {
            issabelInstance.disconnect();
        }
        activeStreams.remove(pbxId);
        System.out.println("Cleaned up resources for PBX: " + pbxId);
    }

    // Helper class to represent PBX events
    public static class PBXEvent {
        private final String eventType;
        private final String queueName;
        private final String data;

        public PBXEvent(String eventType, String queueName, String data) {
            this.eventType = eventType;
            this.queueName = queueName;
            this.data = data;
        }

        public String getEventType() {
            return eventType;
        }

        public String getQueueName() {
            return queueName;
        }

        public String getData() {
            return data;
        }

        @Override
        public String toString() {
            return "PBXEvent{" +
                    "eventType='" + eventType + '\'' +
                    ", queueName='" + queueName + '\'' +
                    ", data='" + data + '\'' +
                    '}';
        }
    }
}
