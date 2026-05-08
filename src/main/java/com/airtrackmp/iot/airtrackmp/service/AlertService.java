package com.airtrackmp.iot.airtrackmp.service;


import com.airtrackmp.iot.airtrackmp.dto.AlertRequest;
import com.airtrackmp.iot.airtrackmp.entity.Alert;
import com.airtrackmp.iot.airtrackmp.entity.Node;
import com.airtrackmp.iot.airtrackmp.entity.Measurement;
import com.airtrackmp.iot.airtrackmp.repository.AlertRepository;
import com.airtrackmp.iot.airtrackmp.repository.MeasurementRepository;
import com.airtrackmp.iot.airtrackmp.repository.NodeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepo;
    private final NodeRepository nodeRepo;
    private final MeasurementRepository measurementRepo;

    public AlertService(AlertRepository alertRepo, NodeRepository nodeRepo, MeasurementRepository measurementRepo) {
        this.alertRepo = alertRepo;
        this.nodeRepo = nodeRepo;
        this.measurementRepo = measurementRepo;
    }

    public Alert saveAlert(AlertRequest request) {
        Node node = nodeRepo.findById(request.getNodeId()).orElseThrow(() -> new RuntimeException("NodeNotFound"));
        Measurement measurement = measurementRepo.findById(request.getMeasurementId()).orElseThrow(() -> new RuntimeException("NodeNotFound"));
        Alert alert = Alert.builder()
                .node(node)
                .measurement(measurement)
                .type(request.getType())
                .message(request.getMessage())
                .createdAt(LocalDateTime.now())
                .build();
        return alertRepo.save(alert);
    }

    public List<Alert> findAllAlerts() {
        return alertRepo.findAll()
                .stream()
                .filter(alert -> !alert.isDeleted())
                .toList();
    }

    public Alert getAlertById(Integer alertId) {
        Alert alert = alertRepo.findById(alertId).orElseThrow(
                () -> new RuntimeException("Alert not Found")
        );

        if (alert.isDeleted()) {
            throw new RuntimeException("Alert is deleted");
        }

        return alert;
    }

    public List<Alert> findAlertsByNodeId(Integer nodeId) {
        Node node = nodeRepo.findById(nodeId)
                .orElseThrow(() -> new RuntimeException("NodeNotFound"));

        return alertRepo.findByNodeId(node.getId())
                .stream()
                .filter(alert -> !alert.isDeleted())
                .toList();
    }

    public Alert findAlertByMeasurement(Integer measurementId) {
        Measurement measurement = measurementRepo.findById(measurementId)
                .orElseThrow(() -> new RuntimeException("NodeNotFound"));

        Alert alert = alertRepo.findByMeasurementId(measurement.getId());

        if (alert == null || alert.isDeleted()) {
            throw new RuntimeException("Alert not Found");
        }

        return alert;
    }

    public Alert updateAlert(Integer alertId, AlertRequest request) {
        Alert alert = getAlertById(alertId);
        if (request.getType() != null) {
            alert.setType(request.getType());
        }
        if (request.getMessage() != null) {
            alert.setMessage(request.getMessage());
        }
        if (request.getNodeId() != null) {
            Node node = nodeRepo.findById(request.getNodeId()).orElseThrow(() -> new RuntimeException("NodeNotFound"));
            alert.setNode(node);
        }
        if (request.getMeasurementId() != null) {
            Measurement measurement = measurementRepo.findById(request.getMeasurementId()).orElseThrow(() -> new RuntimeException("MeasurementNotFound"));
            alert.setMeasurement(measurement);
        }
        return alertRepo.save(alert);
    }

    public void removeAlert(Integer alertId) {
        Alert alert = getAlertById(alertId);
        alert.setDeleted(true);
        alertRepo.save(alert);
    }
}
