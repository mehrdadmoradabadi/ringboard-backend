package com.RingBoard.wallboard.resource.adapters;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.CloseStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public abstract class BaseWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(BaseWebSocketHandler.class);
    protected final ObjectMapper objectMapper;

    public BaseWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        logger.error("WebSocket transport error: ", exception);
        try {
            session.close(CloseStatus.SERVER_ERROR);
        } catch (IOException e) {
            logger.error("Error closing WebSocket session: ", e);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("WebSocket connection established: {}", session.getId());
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        logger.info("WebSocket connection closed: {} with status: {}", session.getId(), status);
    }

    protected void sendErrorMessage(WebSocketSession session, String message) {
        try {
            ErrorMessage errorMessage = new ErrorMessage("ERROR", message);
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(errorMessage)));
        } catch (IOException e) {
            logger.error("Error sending error message: ", e);
        }
    }

    private static class ErrorMessage {
        private final String type;
        private final String message;

        public ErrorMessage(String type, String message) {
            this.type = type;
            this.message = message;
        }

        public String getType() { return type; }
        public String getMessage() { return message; }
    }
}