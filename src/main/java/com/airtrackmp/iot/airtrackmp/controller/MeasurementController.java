package com.airtrackmp.iot.airtrackmp.controller;

import com.airtrackmp.iot.airtrackmp.dto.MeasurementRequest;
import com.airtrackmp.iot.airtrackmp.entity.Measurement;
import com.airtrackmp.iot.airtrackmp.service.MeasurementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/measurements")
public class MeasurementController {

    private final MeasurementService measurementService;

    public MeasurementController(MeasurementService measurementService){
        this.measurementService = measurementService;
    }

    @PostMapping
    public ResponseEntity<Measurement> createMeasurement(@RequestBody MeasurementRequest request){
        Measurement saved = measurementService.saveMeasurement(request);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/node/{nodeId}")
    public ResponseEntity<List<Measurement>> getByNode(@PathVariable Integer nodeId){
        return ResponseEntity.ok(measurementService.getMeasurementsByNode(nodeId));
    }

    @GetMapping("/node/{nodeId}/latest")
    public ResponseEntity<List<Measurement>> getLatest(@PathVariable Integer nodeId){
        return ResponseEntity.ok(measurementService.getLastMeasurements(nodeId));
    }

    @GetMapping
    public ResponseEntity<List<Measurement>> getAll(){
        return ResponseEntity.ok(measurementService.getAllMeasurements());
    }
}
