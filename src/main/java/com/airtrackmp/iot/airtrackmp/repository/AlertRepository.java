package com.airtrackmp.iot.airtrackmp.repository;

import com.airtrackmp.iot.airtrackmp.entity.Alert;

import java.util.List;

public interface AlertRepository {

    List<Alert> findByNodeId(Integer nodeId);
    Alert findByMeasurement(Integer measurementId);

}
