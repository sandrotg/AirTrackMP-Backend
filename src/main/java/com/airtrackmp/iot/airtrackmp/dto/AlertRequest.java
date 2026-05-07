package com.airtrackmp.iot.airtrackmp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlertRequest {
    private Integer nodeId;
    private Integer measurementId;
    private String type;
    private String message;
}
