package com.airtrackmp.iot.airtrackmp.config;

import com.airtrackmp.iot.airtrackmp.service.producers.AlertProducer;
import com.airtrackmp.iot.airtrackmp.service.producers.MeasurementProducer;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic measurementsTopic() {
        return new NewTopic(MeasurementProducer.MEASUREMENTS_TOPIC, 1, (short) 1);
    }

    @Bean
    public NewTopic measurementsBulkTopic() {
        return new NewTopic(MeasurementProducer.MEASUREMENTS_BULK_TOPIC, 1, (short) 1);
    }

    @Bean
    public NewTopic measurementsNodeBulkTopic() {
        return new NewTopic(MeasurementProducer.MEASUREMENTS_NODE_BULK_TOPIC, 1, (short) 1);
    }

    @Bean
    public NewTopic alertsTopic() {
        return new NewTopic(AlertProducer.ALERTS_TOPIC, 1, (short) 1);
    }
}