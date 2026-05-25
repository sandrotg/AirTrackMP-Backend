package com.airtrackmp.iot.airtrackmp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ml.service")
public class MlServiceProperties {

    private String url = "http://localhost:5000";

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
