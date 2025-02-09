package com.RingBoard.wallboard.resource.adapters;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class PBXEvent {
    private String pbxId;
    private String category;
    private String eventType;
    private String deviceId;
    private String status;
    private long timestamp;
    private Map<String, Object> additionalData = new HashMap<>();

}
