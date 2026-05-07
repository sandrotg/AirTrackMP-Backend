package com.airtrackmp.iot.airtrackmp.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum NodeStatus {
    ACTIVE,
    INACTIVE,
    MAINTENANCE,
    OFFLINE,
    CALIBRATION;

    @JsonCreator
    public static NodeStatus from(String value) {
        return NodeStatus.valueOf(value.toUpperCase());
    }
}
