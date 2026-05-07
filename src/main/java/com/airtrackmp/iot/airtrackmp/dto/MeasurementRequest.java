package com.airtrackmp.iot.airtrackmp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MeasurementRequest {

    private Integer nodeId;
    private Float pm25;
    private Float pm10;
    private Float temperature;
    private Float humidity;
    private LocalDateTime recordedAt;
}