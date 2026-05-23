package com.airtrackmp.iot.airtrackmp.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic measurementsTopic() {
        return new NewTopic("measurements-topic", 1, (short) 1);
    }
    @Bean
    public NewTopic measurementsBulkTopic(){return new NewTopic("measurements-bulk-topic", 1, (short) 1);}
    @Bean
    public NewTopic measurementsNodeBulkTopic(){return new NewTopic("measurements-node-bulk-topic", 1, (short) 1);}
}