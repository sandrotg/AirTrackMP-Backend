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

    public MeasurementService(MeasurementRepository measurementRepo, NodeRepository nodeRepo){
        this.measurementRepo = measurementRepo;
        this.nodeRepo = nodeRepo;
    }

    public Measurement saveMeasurement(MeasurementRequest request){
        System.out.println(request.getNodeId());
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

    public List<Measurement> saveMeasurementBulk(List<MeasurementRequest> requests){

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
        return measurementRepo.saveAll(savedMeasurements);
    }


    public List<Measurement> getMeasurementsByNode(Integer nodeId){

        Node node = getActiveNodeOrThrow(nodeId);
        return measurementRepo.findByNodeIdOrderByRecordedAtDesc(nodeId);
    }

    public List<Measurement> getLastMeasurements(Integer nodeId){

        Node node = getActiveNodeOrThrow(nodeId);
        return measurementRepo.findTop10ByNodeIdOrderByRecordedAtDesc(nodeId);
    }

    public List<Measurement> getAllMeasurements(){
        return measurementRepo.findAll();
    }

    public List<MeasurementAverageDto> getAveragesByNode(
            Integer nodeId,
            LocalDateTime from,
            LocalDateTime to,
            String groupBy
    ){
        Node node = getActiveNodeOrThrow(nodeId);
        if (from.isAfter(to)) throw new IllegalArgumentException("Invalid date range");

        return measurementRepo.getAverages(nodeId, from, to, groupBy);
    }

    public List<Measurement> getIntervalMeasurements(
            Integer nodeId,
            LocalDateTime from,
            LocalDateTime to
    ){
        Node node = getActiveNodeOrThrow(nodeId);
        if (from.isAfter(to)) throw new IllegalArgumentException("Invalid date range");

        return measurementRepo.getIntervalMeasurements(nodeId, from, to);
    }

    private Node getActiveNodeOrThrow(Integer nodeId){
        Node node = nodeRepo.findById(nodeId)
                .orElseThrow(() -> new RuntimeException("Node Not Found"));

        if (Boolean.TRUE.equals(node.isDeleted())) {
            throw new RuntimeException("Node Deleted");
        }

        return node;
    }
}
