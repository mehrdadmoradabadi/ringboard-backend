package com.RingBoard.wallboard.resource.asterisk;

import com.RingBoard.wallboard.pbx.PBX;
import com.RingBoard.wallboard.pbx.PBXService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.asteriskjava.manager.AbstractManagerEventListener;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionFactory;
import org.asteriskjava.manager.event.*;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class PBXEventHandler extends TextWebSocketHandler {
    private final PBXService pbxService;
    private final ObjectMapper objectMapper;
    private final ConcurrentMap<String, ManagerConnection> pbxConnections = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Set<WebSocketSession>> sessionsByCategory = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, String> sessionPbxMap = new ConcurrentHashMap<>();

    public PBXEventHandler(PBXService pbxService, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.pbxService = pbxService;
        Arrays.asList("extension", "queue", "trunk", "agent", "all")
                .forEach(category -> sessionsByCategory.put(category, ConcurrentHashMap.newKeySet()));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            String pbxId = extractPbxId(session);
            String category = extractCategory(session);

            PBX pbx = pbxService.findById(pbxId);
            if (pbx == null) {
                session.close(CloseStatus.BAD_DATA);
                return;
            }
            if (!sessionsByCategory.containsKey(category)) {
                session.close(CloseStatus.BAD_DATA);
                return;
            }
            sessionsByCategory.get(category).add(session);
            sessionPbxMap.put(session.getId(), pbxId);
            initializePBXConnection(pbxId);

        } catch (Exception e) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    private String extractPbxId(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query == null) {
            throw new IllegalArgumentException("Missing pbxId parameter");
        }

        Optional<String> pbxId = Arrays.stream(query.split("&"))
                .filter(param -> param.startsWith("pbxId="))
                .map(param -> param.split("=")[1])
                .findFirst();

        return pbxId.orElseThrow(() -> new IllegalArgumentException("Missing pbxId parameter"));
    }

    private String extractCategory(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query == null) {
            throw new IllegalArgumentException("Missing category parameter");
        }

        Optional<String> category = Arrays.stream(query.split("&"))
                .filter(param -> param.startsWith("category="))
                .map(param -> param.split("=")[1])
                .findFirst();

        return category.orElseThrow(() -> new IllegalArgumentException("Missing category parameter"));
    }

    private void initializePBXConnection(String pbxId) {
        pbxConnections.computeIfAbsent(pbxId, id -> {
            try {
                PBX pbx = pbxService.findById(id);
                if (pbx == null) {
                    throw new RuntimeException("PBX not found with ID: " + id);
                }

                ManagerConnectionFactory factory = new ManagerConnectionFactory(
                        pbx.getHost(),
                        pbx.getUsername(),
                        pbx.getPassword()
                );

                ManagerConnection connection = factory.createManagerConnection();
                connection.addEventListener(new PBXEventListener(pbxId));
                connection.login();
                return connection;
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize PBX connection: " + e.getMessage(), e);
            }
        });
    }

    private class PBXEventListener extends AbstractManagerEventListener {
        private final String pbxId;

        public PBXEventListener(String pbxId) {
            this.pbxId = pbxId;
        }

        @Override
        public void onManagerEvent(ManagerEvent event) {
            try {
                if (!(event instanceof PeerStatusEvent) &&
                        !(event instanceof QueueMemberStatusEvent) &&
                        !(event instanceof AgentCallbackLoginEvent) &&
                        !(event instanceof RegistryEvent) &&
                        !(event instanceof QueueCallerAbandonEvent) &&
                        !(event instanceof QueueCallerJoinEvent) &&
                        !(event instanceof AgentCallbackLogoffEvent) &&
                        !(event instanceof AgentConnectEvent) &&
                        !(event instanceof PeerEntryEvent)) {
                    return;
                }
                PBXEvent pbxEvent = new PBXEvent();
                pbxEvent.setPbxId(pbxId);
                pbxEvent.setTimestamp(System.currentTimeMillis());

                if (event instanceof PeerStatusEvent) {
                    handleExtensionEvent((PeerStatusEvent) event, pbxEvent);
                } else if (event instanceof QueueMemberStatusEvent ||
                        event instanceof QueueCallerAbandonEvent ||
                        event instanceof QueueCallerJoinEvent) {
                    handleQueueEvent(event, pbxEvent);
                } else if (event instanceof AgentCallbackLoginEvent ||
                        event instanceof AgentCallbackLogoffEvent ||
                        event instanceof AgentConnectEvent) {
                    handleAgentEvent(event, pbxEvent);
                } else if (event instanceof RegistryEvent ||
                        event instanceof PeerEntryEvent) {
                    handleTrunkEvent(event, pbxEvent);
                }

                if (pbxEvent.getCategory() != null && pbxEvent.getEventType() != null) {
                    broadcastEvent(pbxEvent);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to handle PBX event: " + e.getMessage(), e);
            }
        }
        private void handleExtensionEvent(PeerStatusEvent event, PBXEvent pbxEvent) {
            pbxEvent.setCategory("extension");
            pbxEvent.setEventType("EXTENSION_STATUS");
            pbxEvent.setDeviceId(event.getPeer());
            pbxEvent.setStatus(event.getPeerStatus());

            Map<String, Object> additionalData = new HashMap<>();
            additionalData.put("address", event.getAddress());
            additionalData.put("time", event.getDateReceived());
            additionalData.put("cause", event.getCause());
            pbxEvent.setAdditionalData(additionalData);
        }

        private void handleQueueEvent(ManagerEvent event, PBXEvent pbxEvent) {
            pbxEvent.setCategory("queue");

            if (event instanceof QueueMemberStatusEvent) {
                QueueMemberStatusEvent qEvent = (QueueMemberStatusEvent) event;
                QueueDeviceState deviceState = QueueDeviceState.fromValue(qEvent.getStatus().toString());
                pbxEvent.setEventType("QUEUE_MEMBER_STATUS");
                pbxEvent.setDeviceId(qEvent.getQueue());
                pbxEvent.setStatus(deviceState.name());

                Map<String, Object> additionalData = new HashMap<>();
                additionalData.put("memberInterface", qEvent.getInterface());
                additionalData.put("paused", qEvent.getPaused());
                additionalData.put("penalty", qEvent.getPenalty());
                pbxEvent.setAdditionalData(additionalData);
            }
            else if (event instanceof QueueCallerAbandonEvent) {
                QueueCallerAbandonEvent qEvent = (QueueCallerAbandonEvent) event;
                pbxEvent.setEventType("QUEUE_CALLER_ABANDON");
                pbxEvent.setDeviceId(qEvent.getQueue());
                pbxEvent.setStatus("ABANDONED");

                Map<String, Object> additionalData = new HashMap<>();
                additionalData.put("holdTime", qEvent.getHoldTime());
                additionalData.put("originalPosition", qEvent.getOriginalPosition());
                additionalData.put("callerIdNum", qEvent.getCallerIdNum());
                pbxEvent.setAdditionalData(additionalData);
            }
            else if (event instanceof QueueCallerJoinEvent) {
                QueueCallerJoinEvent qEvent = (QueueCallerJoinEvent) event;
                pbxEvent.setEventType("QUEUE_CALLER_JOIN");
                pbxEvent.setDeviceId(qEvent.getQueue());
                pbxEvent.setStatus("JOINED");

                Map<String, Object> additionalData = new HashMap<>();
                additionalData.put("position", qEvent.getPosition());
                additionalData.put("callerIdNum", qEvent.getCallerIdNum());
                additionalData.put("count", qEvent.getCount());
                pbxEvent.setAdditionalData(additionalData);
            }
        }

        private void handleAgentEvent(ManagerEvent event, PBXEvent pbxEvent) {
            pbxEvent.setCategory("agent");

            if (event instanceof AgentCallbackLoginEvent) {
                AgentCallbackLoginEvent aEvent = (AgentCallbackLoginEvent) event;
                pbxEvent.setEventType("AGENT_LOGIN");
                pbxEvent.setDeviceId(aEvent.getAgent());
                pbxEvent.setStatus("LOGGED_IN");

                Map<String, Object> additionalData = new HashMap<>();
                additionalData.put("loginChan", aEvent.getLoginChan());
                pbxEvent.setAdditionalData(additionalData);
            }
            else if (event instanceof AgentCallbackLogoffEvent) {
                AgentCallbackLogoffEvent aEvent = (AgentCallbackLogoffEvent) event;
                pbxEvent.setEventType("AGENT_LOGOFF");
                pbxEvent.setDeviceId(aEvent.getAgent());
                pbxEvent.setStatus("LOGGED_OUT");
            }
            else if (event instanceof AgentConnectEvent) {
                AgentConnectEvent aEvent = (AgentConnectEvent) event;
                pbxEvent.setEventType("AGENT_CONNECT");
                pbxEvent.setDeviceId(aEvent.getMemberName());
                pbxEvent.setStatus("CONNECTED");

                Map<String, Object> additionalData = new HashMap<>();
                additionalData.put("queue", aEvent.getQueue());
                additionalData.put("holdTime", aEvent.getHoldTime());
                additionalData.put("ringTime", aEvent.getRingtime());
                pbxEvent.setAdditionalData(additionalData);
            }
        }

        private void handleTrunkEvent(ManagerEvent event, PBXEvent pbxEvent) {
            pbxEvent.setCategory("trunk");

            if (event instanceof RegistryEvent) {
                RegistryEvent rEvent = (RegistryEvent) event;
                pbxEvent.setEventType("TRUNK_REGISTRY");
                pbxEvent.setDeviceId(rEvent.getChannel());
                pbxEvent.setStatus(rEvent.getStatus());

                Map<String, Object> additionalData = new HashMap<>();
                additionalData.put("cause", rEvent.getCause());
                additionalData.put("domain", rEvent.getDomain());
                pbxEvent.setAdditionalData(additionalData);
            }
            else if (event instanceof PeerEntryEvent) {
                PeerEntryEvent pEvent = (PeerEntryEvent) event;
                pbxEvent.setEventType("TRUNK_PEER_STATUS");
                pbxEvent.setDeviceId(pEvent.getChannelType() + "/" + pEvent.getObjectName());
                pbxEvent.setStatus(pEvent.getStatus());

                Map<String, Object> additionalData = new HashMap<>();
                additionalData.put("address", pEvent.getIpAddress());
                additionalData.put("port", pEvent.getIpPort());
                additionalData.put("dynamic", pEvent.getDynamic());
                pbxEvent.setAdditionalData(additionalData);
            }
        }

        private void broadcastEvent(PBXEvent event) throws Exception {
            String jsonEvent = objectMapper.writeValueAsString(event);

            Set<WebSocketSession> categorySessions = sessionsByCategory.get(event.getCategory());
            if (categorySessions != null) {
                for (WebSocketSession session : categorySessions) {
                    if (sessionPbxMap.get(session.getId()).equals(pbxId)) {
                        session.sendMessage(new TextMessage(jsonEvent));
                    }
                }
            }

            Set<WebSocketSession> allSessions = sessionsByCategory.get("all");
            if (allSessions != null) {
                for (WebSocketSession session : allSessions) {
                    if (sessionPbxMap.get(session.getId()).equals(pbxId)) {
                        session.sendMessage(new TextMessage(jsonEvent));
                    }
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String pbxId = sessionPbxMap.remove(session.getId());
        sessionsByCategory.values().forEach(sessions -> sessions.remove(session));
        if (pbxId != null && !hasActiveSessions(pbxId)) {
            cleanupPBXConnection(pbxId);
        }
    }

    private boolean hasActiveSessions(String pbxId) {
        return sessionPbxMap.values().stream().anyMatch(id -> id.equals(pbxId));
    }

    private void cleanupPBXConnection(String pbxId) {
        ManagerConnection connection = pbxConnections.remove(pbxId);
        if (connection != null) {
            connection.logoff();
        }
    }
}