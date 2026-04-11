package com.airtrackmp.iot.airtrackmp.service;

import com.airtrackmp.iot.airtrackmp.dto.MeasurementRequest;
import com.airtrackmp.iot.airtrackmp.entity.Measurement;
import com.airtrackmp.iot.airtrackmp.entity.Node;
import com.airtrackmp.iot.airtrackmp.repository.MeasurementRepository;
import com.airtrackmp.iot.airtrackmp.repository.NodeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MeasurementService {

    private final MeasurementRepository measurementRepo;
    private final NodeRepository nodeRepo;

    public MeasurementService(MeasurementRepository measurementRepo, NodeRepository nodeRepo){
        this.measurementRepo = measurementRepo;
        this.nodeRepo = nodeRepo;
    }

    public Measurement saveMeasurement(MeasurementRequest request){
        Node node = nodeRepo.findById(request.getNodeId())
                .orElseThrow(()-> new RuntimeException("NodeNotFound"));
        Measurement measurement = Measurement.builder()
                .node(node)
                .pm25(request.getPm25())
                .pm10(request.getPm10())
                .temperature(request.getTemperature())
                .humidity(request.getHumidity())
                .recordedAt(LocalDateTime.now())
                .build();
        return measurementRepo.save(measurement);
    }

    public List<Measurement> getMeasurementsByNode(Integer nodeId){

        if (!nodeRepo.existsById(nodeId)) {
            throw new RuntimeException("Node not found");
        }

        return measurementRepo.findByNodeIdOrderByRecordedAtDesc(nodeId);
    }

    public List<Measurement> getLastMeasurements(Integer nodeId){

        if (!nodeRepo.existsById(nodeId)) {
            throw new RuntimeException("Node not found");
        }
        return measurementRepo.findTop10ByNodeIdOrderByRecordedAtDesc(nodeId);
    }

    public List<Measurement> getAllMeasurements(){
        return measurementRepo.findAll();
    }
}
