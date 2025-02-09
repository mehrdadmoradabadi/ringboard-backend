package com.RingBoard.wallboard.resource.adapters;

public enum QueueDeviceState {
    DEVICE_UNKNOWN(0),
    DEVICE_NOT_INUSE(1),
    DEVICE_INUSE(2),
    DEVICE_BUSY(3),
    DEVICE_INVALID(4),
    DEVICE_UNAVAILABLE(5),
    DEVICE_RINGING(6),
    DEVICE_RINGINUSE(7),
    DEVICE_ONHOLD(8);

    private final int value;

    QueueDeviceState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static QueueDeviceState fromValue(String value) {
        for (QueueDeviceState state : QueueDeviceState.values()) {
            if (state.value == Integer.parseInt(value)) {
                return state;
            }
        }
        return DEVICE_UNKNOWN;
    }

    public static QueueDeviceState fromString(String state) {
        try {
            return valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            try {
                return fromValue(state);
            } catch (NumberFormatException ex) {
                return DEVICE_UNKNOWN;
            }
        }
    }
}

