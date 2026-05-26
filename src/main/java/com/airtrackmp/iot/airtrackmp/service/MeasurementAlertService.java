package com.airtrackmp.iot.airtrackmp.service;

import com.airtrackmp.iot.airtrackmp.dto.AlertEvent;
import com.airtrackmp.iot.airtrackmp.entity.Measurement;
import com.airtrackmp.iot.airtrackmp.repository.AlertRepository;
import com.airtrackmp.iot.airtrackmp.service.producers.AlertProducer;
import com.airtrackmp.iot.airtrackmp.util.RiskLevelCalculator;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class MeasurementAlertService {

    private static final int CONSECUTIVE_DANGEROUS_THRESHOLD = 7;

    private final AlertRepository alertRepo;
    private final AlertProducer alertProducer;

    public MeasurementAlertService(AlertRepository alertRepo, AlertProducer alertProducer) {
        this.alertRepo = alertRepo;
        this.alertProducer = alertProducer;
    }

    public void evaluateAndPublish(Measurement measurement) {
        if (!hasPmData(measurement)) {
            return;
        }

        float pm25 = measurement.getPm25() != null ? measurement.getPm25() : 0f;
        float pm10 = measurement.getPm10() != null ? measurement.getPm10() : 0f;

        if (!RiskLevelCalculator.requiresAlert(pm25, pm10)) {
            return;
        }

        publishAlertIfAbsent(measurement, pm25, pm10);
    }

    public void evaluateAndPublishAll(List<Measurement> measurements) {
        if (measurements == null || measurements.isEmpty()) {
            return;
        }

        List<Measurement> sorted = measurements.stream()
                .sorted(Comparator.comparing(
                        Measurement::getRecordedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .toList();

        int consecutiveDangerous = 0;
        for (Measurement measurement : sorted) {
            if (!hasPmData(measurement)) {
                consecutiveDangerous = 0;
                continue;
            }

            float pm25 = measurement.getPm25() != null ? measurement.getPm25() : 0f;
            float pm10 = measurement.getPm10() != null ? measurement.getPm10() : 0f;

            if (RiskLevelCalculator.requiresAlert(pm25, pm10)) {
                consecutiveDangerous++;
                if (consecutiveDangerous == CONSECUTIVE_DANGEROUS_THRESHOLD) {
                    publishAlertIfAbsent(measurement, pm25, pm10);
                }
            } else {
                consecutiveDangerous = 0;
            }
        }
    }

    private boolean hasPmData(Measurement measurement) {
        return measurement.getPm25() != null || measurement.getPm10() != null;
    }

    private void publishAlertIfAbsent(Measurement measurement, float pm25, float pm10) {
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
}
