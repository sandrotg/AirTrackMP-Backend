package com.airtrackmp.iot.airtrackmp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MlPredictResponse {

    private String timestamp;

    @JsonProperty("future_pm25")
    private Float futurePm25;

    @JsonProperty("future_pm10")
    private Float futurePm10;
}
