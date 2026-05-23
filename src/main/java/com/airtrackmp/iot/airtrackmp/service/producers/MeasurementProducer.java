package com.airtrackmp.iot.airtrackmp.service.producers;

import com.airtrackmp.iot.airtrackmp.dto.MeasurementRequest;
import com.airtrackmp.iot.airtrackmp.dto.NodeBulkMeasurementRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeasurementProducer {

    private final KafkaTemplate<String, MeasurementRequest> kafkaTemplate;
    private final KafkaTemplate<String, List<MeasurementRequest>> bulkKafkaTemplate;
    private final KafkaTemplate<String, NodeBulkMeasurementRequest> nodeBulkKafkaTemplate;

    public MeasurementProducer(KafkaTemplate<String, MeasurementRequest> kafkaTemplate,
                               KafkaTemplate<String, List<MeasurementRequest>> bulkKafkaTemplate,
                               KafkaTemplate<String, NodeBulkMeasurementRequest> nodeBulkKafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
        this.bulkKafkaTemplate = bulkKafkaTemplate;
        this.nodeBulkKafkaTemplate = nodeBulkKafkaTemplate;
    }

    public void sendMeasurement(MeasurementRequest request){
        kafkaTemplate.send(
                "measurements-topic",
                request
        );
        System.out.println("Measurement event sent");
    }

    public void sendMeasurementBulk(List<MeasurementRequest> requests){
        bulkKafkaTemplate.send("measurements-bulk-topic", requests);
    }

    public void sendNodeMeasurementBulk(NodeBulkMeasurementRequest request){
        nodeBulkKafkaTemplate.send("measurements-node-bulk-topic", request);
    }
}
