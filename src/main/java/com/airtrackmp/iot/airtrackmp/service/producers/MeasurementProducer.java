package com.airtrackmp.iot.airtrackmp.service.producers;

import com.airtrackmp.iot.airtrackmp.dto.BulkMeasurementsRequest;
import com.airtrackmp.iot.airtrackmp.dto.MeasurementRequest;
import com.airtrackmp.iot.airtrackmp.dto.NodeBulkMeasurementRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeasurementProducer {

    private static final Logger log = LoggerFactory.getLogger(MeasurementProducer.class);

    public static final String MEASUREMENTS_TOPIC = "measurements-topic";
    public static final String MEASUREMENTS_BULK_TOPIC = "measurements-bulk-topic";
    public static final String MEASUREMENTS_NODE_BULK_TOPIC = "measurements-node-bulk-topic";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public MeasurementProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMeasurement(MeasurementRequest request) {
        kafkaTemplate.send(MEASUREMENTS_TOPIC, request);
        log.info("Measurement event sent for node {}", request.getNodeId());
    }

    public void sendMeasurementBulk(List<MeasurementRequest> requests) {
        BulkMeasurementsRequest bulkRequest = new BulkMeasurementsRequest();
        bulkRequest.setMeasurements(requests);
        kafkaTemplate.send(MEASUREMENTS_BULK_TOPIC, bulkRequest);
        log.info("Bulk measurement event sent with {} records", requests.size());
    }

    public void sendNodeMeasurementBulk(NodeBulkMeasurementRequest request) {
        kafkaTemplate.send(MEASUREMENTS_NODE_BULK_TOPIC, request);
        log.info("Node bulk measurement event sent for node {}", request.getNodeId());
    }
}
