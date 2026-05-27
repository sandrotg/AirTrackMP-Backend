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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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
    public ObjectMapper kafkaObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory(ObjectMapper kafkaObjectMapper) {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        JsonSerializer<Object> serializer = new JsonSerializer<>(kafkaObjectMapper);
        return new DefaultKafkaProducerFactory<>(config, new StringSerializer(), serializer);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ConsumerFactory<String, MeasurementRequest> measurementConsumerFactory(ObjectMapper kafkaObjectMapper) {
        return buildConsumerFactory(MeasurementRequest.class, kafkaObjectMapper);
    }

    @Bean
    public ConsumerFactory<String, BulkMeasurementsRequest> bulkMeasurementConsumerFactory(ObjectMapper kafkaObjectMapper) {
        return buildConsumerFactory(BulkMeasurementsRequest.class, kafkaObjectMapper);
    }

    @Bean
    public ConsumerFactory<String, NodeBulkMeasurementRequest> nodeBulkMeasurementConsumerFactory(ObjectMapper kafkaObjectMapper) {
        return buildConsumerFactory(NodeBulkMeasurementRequest.class, kafkaObjectMapper);
    }

    @Bean
    public ConsumerFactory<String, AlertEvent> alertConsumerFactory(ObjectMapper kafkaObjectMapper) {
        return buildConsumerFactory(AlertEvent.class, kafkaObjectMapper);
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

    private <T> ConsumerFactory<String, T> buildConsumerFactory(Class<T> targetType, ObjectMapper kafkaObjectMapper) {
        JsonDeserializer<T> deserializer = new JsonDeserializer<>(targetType, kafkaObjectMapper);
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
