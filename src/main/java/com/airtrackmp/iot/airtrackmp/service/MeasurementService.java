package com.airtrackmp.iot.airtrackmp.service;

import com.airtrackmp.iot.airtrackmp.dto.MeasurementAverageDto;
import com.airtrackmp.iot.airtrackmp.dto.MeasurementRequest;
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
public class MeasurementService {

    private final MeasurementRepository measurementRepo;
    private final NodeRepository nodeRepo;

    public MeasurementService(MeasurementRepository measurementRepo, NodeRepository nodeRepo) {
        this.measurementRepo = measurementRepo;
        this.nodeRepo = nodeRepo;
    }

    public Measurement saveMeasurement(MeasurementRequest request) {
        Node node = getActiveNodeOrThrow(request.getNodeId());
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
        return measurementRepo.save(measurement);
    }

    public List<Measurement> saveMeasurementBulk(List<MeasurementRequest> requests) {
        List<Integer> nodeIds = requests.stream()
                .map(MeasurementRequest::getNodeId)
                .distinct()
                .toList();

        List<Node> nodes = nodeRepo.findAllById(nodeIds);

        Map<Integer, Node> nodeMap = nodes.stream()
                .filter(node -> !Boolean.TRUE.equals(node.isDeleted()))
                .collect(Collectors.toMap(Node::getId, node -> node));

        List<Measurement> savedMeasurements = new ArrayList<>();

        for (MeasurementRequest request : requests) {
            Node node = nodeMap.get(request.getNodeId());

            if (node == null) {
                throw new RuntimeException("NodeNotFoundOrDeleted: " + request.getNodeId());
            }

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

        return measurementRepo.saveAll(savedMeasurements);
    }

    public List<Measurement> getMeasurementsByNode(Integer nodeId) {
        getActiveNodeOrThrow(nodeId);
        return measurementRepo.findByNodeIdOrderByRecordedAtDesc(nodeId);
    }

    public List<Measurement> getLastMeasurements(Integer nodeId) {
        getActiveNodeOrThrow(nodeId);
        return measurementRepo.findTop10ByNodeIdOrderByRecordedAtDesc(nodeId);
    }

    public List<Measurement> getAllMeasurements() {
        return measurementRepo.findAll();
    }

    public List<MeasurementAverageDto> getAveragesByNode(
            Integer nodeId,
            LocalDateTime from,
            LocalDateTime to,
            String groupBy
    ) {
        getActiveNodeOrThrow(nodeId);
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Invalid date range");
        }

        return measurementRepo.getAverages(nodeId, from, to, groupBy);
    }

    public List<Measurement> getIntervalMeasurements(
            Integer nodeId,
            LocalDateTime from,
            LocalDateTime to
    ) {
        getActiveNodeOrThrow(nodeId);
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Invalid date range");
        }

        return measurementRepo.getIntervalMeasurements(nodeId, from, to);
    }

    private Node getActiveNodeOrThrow(Integer nodeId) {
        Node node = nodeRepo.findById(nodeId)
                .orElseThrow(() -> new RuntimeException("Node Not Found"));

        if (Boolean.TRUE.equals(node.isDeleted())) {
            throw new RuntimeException("Node Deleted");
        }

        return node;
    }
}
