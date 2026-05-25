package com.airtrackmp.iot.airtrackmp.service;

import com.airtrackmp.iot.airtrackmp.dto.AlertEvent;
import com.airtrackmp.iot.airtrackmp.entity.Measurement;
import com.airtrackmp.iot.airtrackmp.repository.AlertRepository;
import com.airtrackmp.iot.airtrackmp.service.producers.AlertProducer;
import com.airtrackmp.iot.airtrackmp.util.RiskLevelCalculator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeasurementAlertService {

    private final AlertRepository alertRepo;
    private final AlertProducer alertProducer;

    public MeasurementAlertService(AlertRepository alertRepo, AlertProducer alertProducer) {
        this.alertRepo = alertRepo;
        this.alertProducer = alertProducer;
    }

    public void evaluateAndPublish(Measurement measurement) {
        if (measurement.getPm25() == null && measurement.getPm10() == null) {
            return;
        }

        float pm25 = measurement.getPm25() != null ? measurement.getPm25() : 0f;
        float pm10 = measurement.getPm10() != null ? measurement.getPm10() : 0f;

        if (!RiskLevelCalculator.requiresAlert(pm25, pm10)) {
            return;
        }

        if (alertRepo.findByMeasurementId(measurement.getId()) != null) {
            return;
        }

        String level = RiskLevelCalculator.fromPmValues(pm25, pm10);

        AlertEvent event = new AlertEvent();
        event.setNodeId(measurement.getNode().getId());
        event.setMeasurementId(measurement.getId());
        event.setType(level);
        event.setMessage(RiskLevelCalculator.buildAlertMessage(pm25, pm10, level));

        alertProducer.sendAlert(event);
    }

    public void evaluateAndPublishAll(List<Measurement> measurements) {
        measurements.forEach(this::evaluateAndPublish);
    }
}
