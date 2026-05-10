package com.airtrackmp.iot.airtrackmp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PredictionRequest {
    private Integer nodeId;
    private Float predictedPm25;
    private Float predictedPm10;
    private String riskLevel;
    private LocalDateTime predictionTime;
}
