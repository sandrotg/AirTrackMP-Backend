package com.airtrackmp.iot.airtrackmp.service;

import com.airtrackmp.iot.airtrackmp.dto.PredictionRequest;
import com.airtrackmp.iot.airtrackmp.entity.Node;
import com.airtrackmp.iot.airtrackmp.entity.Prediction;
import com.airtrackmp.iot.airtrackmp.repository.NodeRepository;
import com.airtrackmp.iot.airtrackmp.repository.PredictionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PredictionService {

    private final PredictionRepository predictionRepo;
    private final NodeRepository nodeRepo;

    public PredictionService (PredictionRepository predictionRepo, NodeRepository nodeRepo) {
        this.predictionRepo = predictionRepo;
        this.nodeRepo = nodeRepo;
    }

    public Prediction savePrediction (PredictionRequest request) {
        Node node = nodeRepo.findById(request.getNodeId()).orElseThrow(() -> new RuntimeException("NodeNotFound"));
        Prediction prediction = Prediction.builder()
                .node(node)
                .predictedPm25(request.getPredictedPm25())
                .predictedPm10(request.getPredictedPm10())
                .riskLevel(request.getRiskLevel())
                .predictionTime(request.getPredictionTime())
                .createdAt(LocalDateTime.now())
                .build();
        return predictionRepo.save(prediction);
    }

    public List<Prediction> getAllPredictions () {
        return predictionRepo.findAll();
    }

    public Prediction findById (Integer predictionId) {
        return predictionRepo.findById(predictionId).orElseThrow(() -> new RuntimeException("Alert not Found"));
    }

    public List<Prediction> findByNodeId (Integer nodeId) {
        Node node = nodeRepo.findById(nodeId).orElseThrow(() -> new RuntimeException("NodeNotFound"));
        return predictionRepo.findByNodeId(node.getId());
    }

    public Prediction updatePrediction (Integer predictionId, PredictionRequest request) {
        Prediction prediction = findById(predictionId);
        if (request.getPredictedPm25() != null){
            prediction.setPredictedPm25(request.getPredictedPm25());
        }
        if (request.getPredictedPm10() != null){
            prediction.setPredictedPm10(request.getPredictedPm10());
        }
        if (request.getRiskLevel() != null){
            prediction.setRiskLevel(request.getRiskLevel());
        }
        if (request.getPredictionTime() != null){
            prediction.setPredictionTime(request.getPredictionTime());
        }
        return predictionRepo.save(prediction);
    }

    public void deletePrediction (Integer predictionId) {
        Prediction prediction = predictionRepo.findById(predictionId).orElseThrow(() -> new RuntimeException("Prediction not found"));
        predictionRepo.delete(prediction);
    }
}
