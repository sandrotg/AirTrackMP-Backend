package com.airtrackmp.iot.airtrackmp.controller;

import com.airtrackmp.iot.airtrackmp.dto.PredictionRequest;
import com.airtrackmp.iot.airtrackmp.entity.Prediction;
import com.airtrackmp.iot.airtrackmp.service.PredictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/prediction")
public class PredictionController {
    private final PredictionService predictionService;

    public PredictionController (PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping
    public ResponseEntity<Prediction> createPrediction(@RequestBody PredictionRequest request) {
        return ResponseEntity.ok(predictionService.savePrediction(request));
    }

    @PostMapping("/generate/node/{nodeId}")
    public ResponseEntity<Prediction> generateForNode(@PathVariable Integer nodeId) {
        return ResponseEntity.ok(predictionService.generateForLatestMeasurement(nodeId));
    }

    @GetMapping
    public ResponseEntity<List<Prediction>> findAll () {
        return ResponseEntity.ok(predictionService.getAllPredictions());
    }

    @GetMapping("/node/{nodeId}")
    public ResponseEntity<List<Prediction>> findByNodeId (@PathVariable Integer nodeId) {
        return ResponseEntity.ok(predictionService.findByNodeId(nodeId));
    }

    @GetMapping("/{predictionId}")
    public  ResponseEntity<Prediction> findById (@PathVariable Integer predictionId) {
        return ResponseEntity.ok(predictionService.findById(predictionId));
    }

    @PutMapping("/{predictionId}")
    public  ResponseEntity<Prediction> updatePrediction (@PathVariable Integer predictionId, @RequestBody PredictionRequest request ) {
        return ResponseEntity.ok(predictionService.updatePrediction(predictionId, request));
    }

    @DeleteMapping("/{predictionId}")
    public ResponseEntity<Void> deletePrediction (@PathVariable Integer predictionId) {
        predictionService.deletePrediction(predictionId);
        return ResponseEntity.noContent().build();
    }
}
