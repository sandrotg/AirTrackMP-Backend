package com.airtrackmp.iot.airtrackmp.repository;

import com.airtrackmp.iot.airtrackmp.entity.Alert;
import com.airtrackmp.iot.airtrackmp.entity.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PredictionRepository extends JpaRepository<Prediction, Integer> {
    List<Prediction> findByNodeId(Integer nodeId);
}
