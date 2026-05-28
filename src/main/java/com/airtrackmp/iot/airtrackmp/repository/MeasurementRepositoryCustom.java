package com.airtrackmp.iot.airtrackmp.repository;

import com.airtrackmp.iot.airtrackmp.dto.MeasurementAverageDto;
import com.airtrackmp.iot.airtrackmp.entity.Measurement;

import java.time.LocalDateTime;
import java.util.List;

public interface MeasurementRepositoryCustom {

    List<MeasurementAverageDto> getAverages(
            Integer nodeId,
            LocalDateTime from,
            LocalDateTime to,
            String groupBy
    );

    List<Measurement> getIntervalMeasurements(
            Integer nodeId,
            LocalDateTime from,
            LocalDateTime to
    );

    List<Measurement> getIntervalMeasurements(
            LocalDateTime from,
            LocalDateTime to
    );
}
