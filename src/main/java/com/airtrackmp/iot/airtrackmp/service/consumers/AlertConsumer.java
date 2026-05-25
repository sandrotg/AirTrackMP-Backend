package com.airtrackmp.iot.airtrackmp.service.consumers;

import com.airtrackmp.iot.airtrackmp.dto.AlertEvent;
import com.airtrackmp.iot.airtrackmp.entity.Alert;
import com.airtrackmp.iot.airtrackmp.entity.Measurement;
import com.airtrackmp.iot.airtrackmp.entity.Node;
import com.airtrackmp.iot.airtrackmp.repository.AlertRepository;
import com.airtrackmp.iot.airtrackmp.repository.MeasurementRepository;
import com.airtrackmp.iot.airtrackmp.repository.NodeRepository;
import com.airtrackmp.iot.airtrackmp.service.producers.AlertProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AlertConsumer {

    private static final Logger log = LoggerFactory.getLogger(AlertConsumer.class);

    private final AlertRepository alertRepo;
    private final NodeRepository nodeRepo;
    private final MeasurementRepository measurementRepo;

    public AlertConsumer(
            AlertRepository alertRepo,
            NodeRepository nodeRepo,
            MeasurementRepository measurementRepo
    ) {
        this.alertRepo = alertRepo;
        this.nodeRepo = nodeRepo;
        this.measurementRepo = measurementRepo;
    }

    @KafkaListener(
            topics = AlertProducer.ALERTS_TOPIC,
            groupId = "airtrack-group",
            containerFactory = "alertKafkaListenerContainerFactory"
    )
    public void consumeAlert(AlertEvent event) {
        if (alertRepo.findByMeasurementId(event.getMeasurementId()) != null) {
            return;
        }

        Node node = nodeRepo.findById(event.getNodeId())
                .orElseThrow(() -> new RuntimeException("NodeNotFound: " + event.getNodeId()));
        Measurement measurement = measurementRepo.findById(event.getMeasurementId())
                .orElseThrow(() -> new RuntimeException("MeasurementNotFound: " + event.getMeasurementId()));

        Alert alert = Alert.builder()
                .node(node)
                .measurement(measurement)
                .type(event.getType())
                .message(event.getMessage())
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .build();

        alertRepo.save(alert);
        log.info("Alert persisted [{}] for node {}", event.getType(), event.getNodeId());
    }
}
