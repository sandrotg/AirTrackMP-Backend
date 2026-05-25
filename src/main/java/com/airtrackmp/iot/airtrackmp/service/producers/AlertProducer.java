package com.airtrackmp.iot.airtrackmp.service.producers;

import com.airtrackmp.iot.airtrackmp.dto.AlertEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AlertProducer {

    public static final String ALERTS_TOPIC = "alerts-topic";

    private static final Logger log = LoggerFactory.getLogger(AlertProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AlertProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAlert(AlertEvent event) {
        kafkaTemplate.send(ALERTS_TOPIC, event);
        log.info("Alert event sent for node {} measurement {}", event.getNodeId(), event.getMeasurementId());
    }
}
