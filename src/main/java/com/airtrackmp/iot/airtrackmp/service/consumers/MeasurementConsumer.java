package com.airtrackmp.iot.airtrackmp.service.consumers;

import com.airtrackmp.iot.airtrackmp.dto.BulkMeasurementsRequest;
import com.airtrackmp.iot.airtrackmp.dto.MeasurementRequest;
import com.airtrackmp.iot.airtrackmp.dto.NodeBulkMeasurementRequest;
import com.airtrackmp.iot.airtrackmp.entity.Measurement;
import com.airtrackmp.iot.airtrackmp.service.MeasurementAlertService;
import com.airtrackmp.iot.airtrackmp.service.MeasurementPersistenceService;
import com.airtrackmp.iot.airtrackmp.service.producers.MeasurementProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeasurementConsumer {

    private static final Logger log = LoggerFactory.getLogger(MeasurementConsumer.class);

    private final MeasurementPersistenceService measurementPersistenceService;
    private final MeasurementAlertService measurementAlertService;

    public MeasurementConsumer(
            MeasurementPersistenceService measurementPersistenceService,
            MeasurementAlertService measurementAlertService
    ) {
        this.measurementPersistenceService = measurementPersistenceService;
        this.measurementAlertService = measurementAlertService;
    }

    @KafkaListener(
            topics = MeasurementProducer.MEASUREMENTS_TOPIC,
            groupId = "airtrack-group",
            containerFactory = "measurementKafkaListenerContainerFactory"
    )
    public void consumeMeasurement(MeasurementRequest request) {
        Measurement saved = measurementPersistenceService.save(request);
        measurementAlertService.evaluateAndPublish(saved);
        log.info("Measurement persisted for node {}", request.getNodeId());
    }

    @KafkaListener(
            topics = MeasurementProducer.MEASUREMENTS_BULK_TOPIC,
            groupId = "airtrack-group",
            containerFactory = "bulkMeasurementKafkaListenerContainerFactory"
    )
    public void consumeBulkMeasurements(BulkMeasurementsRequest bulkRequest) {
        List<Measurement> saved = measurementPersistenceService.saveBulk(bulkRequest.getMeasurements());
        measurementAlertService.evaluateAndPublishAll(saved);
        log.info("Bulk measurements persisted: {}", bulkRequest.getMeasurements().size());
    }

    @KafkaListener(
            topics = MeasurementProducer.MEASUREMENTS_NODE_BULK_TOPIC,
            groupId = "airtrack-group",
            containerFactory = "nodeBulkMeasurementKafkaListenerContainerFactory"
    )
    public void consumeNodeBulkMeasurements(NodeBulkMeasurementRequest bulkRequest) {
        List<Measurement> saved = measurementPersistenceService.saveNodeBulk(bulkRequest);
        measurementAlertService.evaluateAndPublishAll(saved);
        log.info("Node bulk measurements persisted for node {}", bulkRequest.getNodeId());
    }
}
