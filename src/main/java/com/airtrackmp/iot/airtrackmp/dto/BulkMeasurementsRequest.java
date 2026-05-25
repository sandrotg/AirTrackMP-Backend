package com.airtrackmp.iot.airtrackmp.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BulkMeasurementsRequest {

    private List<MeasurementRequest> measurements;
}
