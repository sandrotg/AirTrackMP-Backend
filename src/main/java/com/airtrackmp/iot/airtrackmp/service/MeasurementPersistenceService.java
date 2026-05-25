package com.airtrackmp.iot.airtrackmp.service;

import com.airtrackmp.iot.airtrackmp.dto.MeasurementRequest;
import com.airtrackmp.iot.airtrackmp.dto.NodeBulkMeasurementRequest;
import com.airtrackmp.iot.airtrackmp.dto.NodeMeasurementRequest;
import com.airtrackmp.iot.airtrackmp.entity.Measurement;
import com.airtrackmp.iot.airtrackmp.entity.Node;
import com.airtrackmp.iot.airtrackmp.repository.MeasurementRepository;
import com.airtrackmp.iot.airtrackmp.repository.NodeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MeasurementPersistenceService {

    private final MeasurementRepository measurementRepo;
    private final NodeRepository nodeRepo;
    private final NodeService nodeService;

    public MeasurementPersistenceService(
            MeasurementRepository measurementRepo,
            NodeRepository nodeRepo,
            NodeService nodeService
    ) {
        this.measurementRepo = measurementRepo;
        this.nodeRepo = nodeRepo;
        this.nodeService = nodeService;
    }

    public Measurement save(MeasurementRequest request) {
        Node node = nodeService.getActiveNodeOrThrow(request.getNodeId());
        return measurementRepo.save(buildMeasurement(node, request));
    }

    public List<Measurement> saveBulk(List<MeasurementRequest> requests) {
        List<Integer> nodeIds = requests.stream()
                .map(MeasurementRequest::getNodeId)
                .distinct()
                .toList();

        Map<Integer, Node> nodeMap = nodeRepo.findAllById(nodeIds).stream()
                .filter(node -> !Boolean.TRUE.equals(node.isDeleted()))
                .collect(Collectors.toMap(Node::getId, node -> node));

        List<Measurement> measurements = new ArrayList<>();

        for (MeasurementRequest request : requests) {
            Node node = nodeMap.get(request.getNodeId());
            if (node == null) {
                throw new RuntimeException("NodeNotFoundOrDeleted: " + request.getNodeId());
            }
            measurements.add(buildMeasurement(node, request));
        }

        return measurementRepo.saveAll(measurements);
    }

    public List<Measurement> saveNodeBulk(NodeBulkMeasurementRequest bulkRequest) {
        Node node = nodeService.getActiveNodeOrThrow(bulkRequest.getNodeId());
        List<Measurement> measurements = new ArrayList<>();

        for (NodeMeasurementRequest request : bulkRequest.getMeasurements()) {
            measurements.add(buildMeasurement(node, request));
        }

        return measurementRepo.saveAll(measurements);
    }

    private Measurement buildMeasurement(Node node, MeasurementRequest request) {
        return Measurement.builder()
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
    }

    private Measurement buildMeasurement(Node node, NodeMeasurementRequest request) {
        return Measurement.builder()
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
    }
}
