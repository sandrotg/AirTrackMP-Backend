package com.airtrackmp.iot.airtrackmp.service;

import com.airtrackmp.iot.airtrackmp.dto.MeasurementAverageDto;
import com.airtrackmp.iot.airtrackmp.dto.MeasurementRequest;
import com.airtrackmp.iot.airtrackmp.dto.NodeBulkMeasurementRequest;
import com.airtrackmp.iot.airtrackmp.dto.NodeMeasurementRequest;
import com.airtrackmp.iot.airtrackmp.entity.Measurement;
import com.airtrackmp.iot.airtrackmp.repository.MeasurementRepository;
import com.airtrackmp.iot.airtrackmp.service.producers.MeasurementProducer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MeasurementService {

    private final MeasurementRepository measurementRepo;
    private final MeasurementProducer measurementProducer;
    private final NodeService nodeService;

    public MeasurementService(
            MeasurementRepository measurementRepo,
            MeasurementProducer measurementProducer,
            NodeService nodeService
    ) {
        this.measurementRepo = measurementRepo;
        this.measurementProducer = measurementProducer;
        this.nodeService = nodeService;
    }

    public void saveMeasurement(MeasurementRequest request) {
        nodeService.getActiveNodeOrThrow(request.getNodeId());
        measurementProducer.sendMeasurement(request);
    }

    public void saveMeasurementBulk(List<MeasurementRequest> requests) {
        measurementProducer.sendMeasurementBulk(requests);
    }

    public void saveMeasurementBulkByNode(Integer nodeId, List<NodeMeasurementRequest> requests) {
        nodeService.getActiveNodeOrThrow(nodeId);
        NodeBulkMeasurementRequest bulkRequest = new NodeBulkMeasurementRequest();
        bulkRequest.setNodeId(nodeId);
        bulkRequest.setMeasurements(requests);
        measurementProducer.sendNodeMeasurementBulk(bulkRequest);
    }

    public List<Measurement> getMeasurementsByNode(Integer nodeId) {
        nodeService.getActiveNodeOrThrow(nodeId);
        return measurementRepo.findByNodeIdOrderByRecordedAtDesc(nodeId);
    }

    public List<Measurement> getLastMeasurements(Integer nodeId) {
        nodeService.getActiveNodeOrThrow(nodeId);
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
        nodeService.getActiveNodeOrThrow(nodeId);
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
        nodeService.getActiveNodeOrThrow(nodeId);
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Invalid date range");
        }

        return measurementRepo.getIntervalMeasurements(nodeId, from, to);
    }

    public List<Measurement> getIntervalMeasurements(LocalDateTime from, LocalDateTime to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Invalid date range");
        }

        return measurementRepo.getIntervalMeasurements(from, to);
    }
}
