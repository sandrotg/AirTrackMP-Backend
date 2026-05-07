package com.airtrackmp.iot.airtrackmp.controller;

import com.airtrackmp.iot.airtrackmp.dto.MeasurementAverageDto;
import com.airtrackmp.iot.airtrackmp.dto.MeasurementRequest;
import com.airtrackmp.iot.airtrackmp.entity.Measurement;
import com.airtrackmp.iot.airtrackmp.service.MeasurementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @PostMapping("/bulk")
    public ResponseEntity<List<Measurement>> createMeasurementsBulk(@RequestBody List<MeasurementRequest> requests){
        List<Measurement> listSaved = measurementService.saveMeasurementBulk(requests);
        return ResponseEntity.ok(listSaved);
    }

    @GetMapping("/node/{nodeId}/latest")
    public ResponseEntity<List<Measurement>> getLatest(@PathVariable Integer nodeId){
        return ResponseEntity.ok(measurementService.getLastMeasurements(nodeId));
    }

    @GetMapping
    public ResponseEntity<List<Measurement>> getAll(){
        return ResponseEntity.ok(measurementService.getAllMeasurements());
    }

    @GetMapping("/node/{nodeId}")
    public ResponseEntity<List<Measurement>> getInterval(
            @PathVariable Integer nodeId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ){
        if(from != null && to != null){
            return ResponseEntity.ok(measurementService.getIntervalMeasurements(
                    nodeId,
                    LocalDateTime.parse(from),
                    LocalDateTime.parse(to)
            ));
        }
        return ResponseEntity.ok(measurementService.getMeasurementsByNode(nodeId));
    }

    @GetMapping("/node/{nodeId}/average")
    public ResponseEntity<List<MeasurementAverageDto>> getAverages(
            @PathVariable Integer nodeId,
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String groupBy
    ){
        return ResponseEntity.ok(
                measurementService.getAveragesByNode(
                        nodeId,
                        LocalDateTime.parse(from),
                        LocalDateTime.parse(to),
                        groupBy
                )
        );
    }

}
