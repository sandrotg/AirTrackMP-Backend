package com.airtrackmp.iot.airtrackmp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MlPredictRequest {

    private LocalDateTime timestamp;
    private Float temperature;
    private Float humidity;
    private Float pm25;
    private Float pm10;

    @JsonProperty("pm25_prev1")
    private Float pm25Prev1;

    @JsonProperty("pm25_prev2")
    private Float pm25Prev2;

    @JsonProperty("pm10_prev1")
    private Float pm10Prev1;

    @JsonProperty("pm10_prev2")
    private Float pm10Prev2;
}
