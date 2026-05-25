package com.airtrackmp.iot.airtrackmp.service;

import com.airtrackmp.iot.airtrackmp.client.MlPredictionClient;
import com.airtrackmp.iot.airtrackmp.dto.MlPredictRequest;
import com.airtrackmp.iot.airtrackmp.dto.MlPredictResponse;
import com.airtrackmp.iot.airtrackmp.dto.PredictionRequest;
import com.airtrackmp.iot.airtrackmp.entity.Measurement;
import com.airtrackmp.iot.airtrackmp.entity.Node;
import com.airtrackmp.iot.airtrackmp.entity.Prediction;
import com.airtrackmp.iot.airtrackmp.repository.MeasurementRepository;
import com.airtrackmp.iot.airtrackmp.repository.NodeRepository;
import com.airtrackmp.iot.airtrackmp.repository.PredictionRepository;
import com.airtrackmp.iot.airtrackmp.util.RiskLevelCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PredictionService {

    private static final Logger log = LoggerFactory.getLogger(PredictionService.class);

    private final PredictionRepository predictionRepo;
    private final NodeRepository nodeRepo;
    private final MeasurementRepository measurementRepo;
    private final MlPredictionClient mlPredictionClient;

    public PredictionService(
            PredictionRepository predictionRepo,
            NodeRepository nodeRepo,
            MeasurementRepository measurementRepo,
            MlPredictionClient mlPredictionClient
    ) {
        this.predictionRepo = predictionRepo;
        this.nodeRepo = nodeRepo;
        this.measurementRepo = measurementRepo;
        this.mlPredictionClient = mlPredictionClient;
    }

    public Prediction savePrediction(PredictionRequest request) {
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

    public Optional<Prediction> generatePredictionForMeasurement(
            Measurement measurement,
            Float pm25Prev1,
            Float pm25Prev2,
            Float pm10Prev1,
            Float pm10Prev2
    ) {
        try {
            MlPredictRequest mlRequest = new MlPredictRequest();
            mlRequest.setTimestamp(measurement.getRecordedAt());
            mlRequest.setTemperature(measurement.getTemperature());
            mlRequest.setHumidity(measurement.getHumidity());
            mlRequest.setPm25(measurement.getPm25());
            mlRequest.setPm10(measurement.getPm10());
            mlRequest.setPm25Prev1(pm25Prev1);
            mlRequest.setPm25Prev2(pm25Prev2);
            mlRequest.setPm10Prev1(pm10Prev1);
            mlRequest.setPm10Prev2(pm10Prev2);

            MlPredictResponse response = mlPredictionClient.predict(mlRequest);

            PredictionRequest request = new PredictionRequest();
            request.setNodeId(measurement.getNode().getId());
            request.setPredictedPm25(response.getFuturePm25());
            request.setPredictedPm10(response.getFuturePm10());
            request.setRiskLevel(
                    RiskLevelCalculator.fromPmValues(response.getFuturePm25(), response.getFuturePm10())
            );
            request.setPredictionTime(measurement.getRecordedAt().plusHours(1));

            return Optional.of(savePrediction(request));
        } catch (RuntimeException exception) {
            log.warn(
                    "Could not generate prediction for node {}: {}",
                    measurement.getNode().getId(),
                    exception.getMessage()
            );
            return Optional.empty();
        }
    }

    public Prediction generateForLatestMeasurement(Integer nodeId) {
        Node node = nodeRepo.findById(nodeId).orElseThrow(() -> new RuntimeException("NodeNotFound"));

        List<Measurement> latestMeasurements =
                measurementRepo.findTop3ByNodeIdOrderByRecordedAtDesc(node.getId());

        if (latestMeasurements.isEmpty()) {
            throw new RuntimeException("No measurements found for node");
        }

        Measurement current = latestMeasurements.get(0);
        Float pm25Prev1 = latestMeasurements.size() > 1
                ? latestMeasurements.get(1).getPm25()
                : current.getPm25();
        Float pm25Prev2 = latestMeasurements.size() > 2
                ? latestMeasurements.get(2).getPm25()
                : pm25Prev1;
        Float pm10Prev1 = latestMeasurements.size() > 1
                ? latestMeasurements.get(1).getPm10()
                : current.getPm10();
        Float pm10Prev2 = latestMeasurements.size() > 2
                ? latestMeasurements.get(2).getPm10()
                : pm10Prev1;

        return generatePredictionForMeasurement(current, pm25Prev1, pm25Prev2, pm10Prev1, pm10Prev2)
                .orElseThrow(() -> new RuntimeException("ML service unavailable"));
    }

    public List<Prediction> getAllPredictions() {
        return predictionRepo.findAll();
    }

    public Prediction findById(Integer predictionId) {
        return predictionRepo.findById(predictionId).orElseThrow(() -> new RuntimeException("Alert not Found"));
    }

    public List<Prediction> findByNodeId(Integer nodeId) {
        Node node = nodeRepo.findById(nodeId).orElseThrow(() -> new RuntimeException("NodeNotFound"));
        return predictionRepo.findByNodeId(node.getId());
    }

    public Prediction updatePrediction(Integer predictionId, PredictionRequest request) {
        Prediction prediction = findById(predictionId);
        if (request.getPredictedPm25() != null) {
            prediction.setPredictedPm25(request.getPredictedPm25());
        }
        if (request.getPredictedPm10() != null) {
            prediction.setPredictedPm10(request.getPredictedPm10());
        }
        if (request.getRiskLevel() != null) {
            prediction.setRiskLevel(request.getRiskLevel());
        }
        if (request.getPredictionTime() != null) {
            prediction.setPredictionTime(request.getPredictionTime());
        }
        return predictionRepo.save(prediction);
    }

    public void deletePrediction(Integer predictionId) {
        Prediction prediction = predictionRepo.findById(predictionId)
                .orElseThrow(() -> new RuntimeException("Prediction not found"));
        predictionRepo.delete(prediction);
    }
}
