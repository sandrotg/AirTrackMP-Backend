package com.airtrackmp.iot.airtrackmp.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MeasurementAverageDto {

    private LocalDateTime period;
    private double avgPm25;
    private double avgPm10;
    private double avgTemperature;
    private double avgHumidity;

    public MeasurementAverageDto(LocalDateTime period, double avgPm25, double avgPm10, double avgTemperature, double avgHumidity){
        this.period = period;
        this.avgPm25 = avgPm25;
        this.avgPm10 = avgPm10;
        this.avgTemperature = avgTemperature;
        this.avgHumidity = avgHumidity;
    }

}
