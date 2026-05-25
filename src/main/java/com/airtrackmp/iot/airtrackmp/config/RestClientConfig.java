package com.airtrackmp.iot.airtrackmp.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(MlServiceProperties.class)
public class RestClientConfig {

    @Bean
    public RestClient mlRestClient(MlServiceProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getUrl())
                .build();
    }
}
