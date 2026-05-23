package com.airtrackmp.iot.airtrackmp.service.consumers;

import com.airtrackmp.iot.airtrackmp.dto.MeasurementRequest;
import com.airtrackmp.iot.airtrackmp.dto.NodeBulkMeasurementRequest;
import com.airtrackmp.iot.airtrackmp.dto.NodeMeasurementRequest;
import com.airtrackmp.iot.airtrackmp.entity.Measurement;
import com.airtrackmp.iot.airtrackmp.entity.Node;
import com.airtrackmp.iot.airtrackmp.repository.MeasurementRepository;
import com.airtrackmp.iot.airtrackmp.repository.NodeRepository;
import com.airtrackmp.iot.airtrackmp.service.NodeService;
import org.springframework.kafka.annotation.KafkaListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MeasurementConsumer {

    private final MeasurementRepository measurementRepo;
    private final NodeRepository nodeRepo;
    private final NodeService nodeService;

    public MeasurementConsumer(MeasurementRepository measurementRepo, NodeRepository nodeRepo, NodeService nodeService){
        this.measurementRepo = measurementRepo;
        this.nodeRepo = nodeRepo;
        this.nodeService = nodeService;
    }

    @KafkaListener(topics = "measurements-topic", groupId = "airtrack-group")
    public void consumeMeasurement(MeasurementRequest request){
        Node node = nodeService.getActiveNodeOrThrow(request.getNodeId());
        Measurement measurement = Measurement.builder()
                .node(node)
                .pm25(request.getPm25())
                .pm10(request.getPm10())
                .temperature(request.getTemperature())
                .humidity(request.getHumidity())
                .recordedAt(
                        request.getRecordedAt() != null
                                ? request.getRecordedAt()
                                : LocalDateTime.now()
                )
                .build();
        measurementRepo.save(measurement);
    }

    @KafkaListener(topics = "measurements-bulk-topic", groupId = "airtrack-group")
    public void consumeBulkMeasurements(List<MeasurementRequest> requests){
        // 1. Obtener IDs únicos
        List<Integer> nodeIds = requests.stream()
                .map(request -> request.getNodeId())
                .distinct()
                .toList();

        // 2. Traer nodos en una sola query
        List<Node> nodes = nodeRepo.findAllById(nodeIds);

        // 3. Convertir a mapa
        Map<Integer, Node> nodeMap = nodes.stream()
                .filter(node -> !Boolean.TRUE.equals(node.isDeleted()))
                .collect(Collectors.toMap(Node::getId, node -> node)); //los :: son methond reference, una forma abreviada de indicar que se ejecuta en la iteracion, evitando escribir node -> node.getId

        List<Measurement> savedMeasurements = new ArrayList<>();

        for(MeasurementRequest request: requests){
            Node node = nodeMap.get(request.getNodeId());

            if (node == null) throw new RuntimeException("NodeNotFoundOrDeleted: " + request.getNodeId());

            Measurement measurement = Measurement.builder()
                    .node(node)
                    .pm25(request.getPm25())
                    .pm10(request.getPm10())
                    .temperature(request.getTemperature())
                    .humidity(request.getHumidity())
                    .recordedAt(
                            request.getRecordedAt() != null
                                    ? request.getRecordedAt()
                                    : LocalDateTime.now()
                    ).build();
            savedMeasurements.add(measurement);
        }
        measurementRepo.saveAll(savedMeasurements);
    }

    @KafkaListener(topics = "measurements-node-bulk-topic", groupId = "airtrack-group")
    public void consumeNodeBulkMeasurements(NodeBulkMeasurementRequest bulkRequest){
        Integer nodeId = bulkRequest.getNodeId();

        Node node = nodeService.getActiveNodeOrThrow(nodeId);

        List<Measurement> measurements = new ArrayList<>();

        for(NodeMeasurementRequest request :
                bulkRequest.getMeasurements()){

            Measurement measurement = Measurement.builder()
                    .node(node)
                    .pm25(request.getPm25())
                    .pm10(request.getPm10())
                    .temperature(request.getTemperature())
                    .humidity(request.getHumidity())
                    .recordedAt(
                            request.getRecordedAt() != null
                                    ? request.getRecordedAt()
                                    : LocalDateTime.now()
                    )
                    .build();

            measurements.add(measurement);
        }

        measurementRepo.saveAll(measurements);
    }
}
