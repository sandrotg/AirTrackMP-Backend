package com.airtrackmp.iot.airtrackmp.config;

import com.airtrackmp.iot.airtrackmp.dto.AlertEvent;
import com.airtrackmp.iot.airtrackmp.dto.BulkMeasurementsRequest;
import com.airtrackmp.iot.airtrackmp.dto.MeasurementRequest;
import com.airtrackmp.iot.airtrackmp.dto.NodeBulkMeasurementRequest;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ConsumerFactory<String, MeasurementRequest> measurementConsumerFactory() {
        return buildConsumerFactory(MeasurementRequest.class);
    }

    @Bean
    public ConsumerFactory<String, BulkMeasurementsRequest> bulkMeasurementConsumerFactory() {
        return buildConsumerFactory(BulkMeasurementsRequest.class);
    }

    @Bean
    public ConsumerFactory<String, NodeBulkMeasurementRequest> nodeBulkMeasurementConsumerFactory() {
        return buildConsumerFactory(NodeBulkMeasurementRequest.class);
    }

    @Bean
    public ConsumerFactory<String, AlertEvent> alertConsumerFactory() {
        return buildConsumerFactory(AlertEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MeasurementRequest> measurementKafkaListenerContainerFactory(
            ConsumerFactory<String, MeasurementRequest> measurementConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, MeasurementRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(measurementConsumerFactory);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BulkMeasurementsRequest> bulkMeasurementKafkaListenerContainerFactory(
            ConsumerFactory<String, BulkMeasurementsRequest> bulkMeasurementConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, BulkMeasurementsRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(bulkMeasurementConsumerFactory);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NodeBulkMeasurementRequest> nodeBulkMeasurementKafkaListenerContainerFactory(
            ConsumerFactory<String, NodeBulkMeasurementRequest> nodeBulkMeasurementConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, NodeBulkMeasurementRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(nodeBulkMeasurementConsumerFactory);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AlertEvent> alertKafkaListenerContainerFactory(
            ConsumerFactory<String, AlertEvent> alertConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, AlertEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(alertConsumerFactory);
        return factory;
    }

    private <T> ConsumerFactory<String, T> buildConsumerFactory(Class<T> targetType) {
        JsonDeserializer<T> deserializer = new JsonDeserializer<>(targetType);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }
}
