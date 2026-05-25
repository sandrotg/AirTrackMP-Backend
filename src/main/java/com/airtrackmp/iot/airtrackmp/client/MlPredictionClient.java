package com.airtrackmp.iot.airtrackmp.client;

import com.airtrackmp.iot.airtrackmp.dto.MlPredictRequest;
import com.airtrackmp.iot.airtrackmp.dto.MlPredictResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class MlPredictionClient {

    private final RestClient restClient;

    public MlPredictionClient(RestClient mlRestClient) {
        this.restClient = mlRestClient;
    }

    public MlPredictResponse predict(MlPredictRequest request) {
        try {
            return restClient.post()
                    .uri("/predict")
                    .body(request)
                    .retrieve()
                    .body(MlPredictResponse.class);
        } catch (RestClientException exception) {
            throw new RuntimeException("ML service unavailable", exception);
        }
    }
}
