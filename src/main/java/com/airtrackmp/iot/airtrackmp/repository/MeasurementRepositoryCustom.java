package com.airtrackmp.iot.airtrackmp.repository;

import com.airtrackmp.iot.airtrackmp.dto.MeasurementAverageDto;

import java.time.LocalDateTime;
import java.util.List;

public interface MeasurementRepositoryCustom {

    List<MeasurementAverageDto> getAverages(
            Integer nodeId,
            LocalDateTime from,
            LocalDateTime to,
            String groupBy
    );
}
