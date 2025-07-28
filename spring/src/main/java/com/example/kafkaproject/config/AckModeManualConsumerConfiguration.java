package com.example.kafkaproject.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

@Configuration
@EnableKafka
public class AckModeManualConsumerConfiguration {
  @Value("${confluent.consumer.bootstrapServers}")
  private String bootstrapServers;

  @Bean("ackModeManualKafkaConsumerFactory")
  public ConsumerFactory<String, String> ackModeManualConsumerFactory() {
    return new DefaultKafkaConsumerFactory<>(ackModeManualConsumerConfig());
  }

  @Bean("ackModeManualKafkaContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, String>
      ackModeManualKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, String> container =
        new ConcurrentKafkaListenerContainerFactory<>();
    container.setConsumerFactory(ackModeManualConsumerFactory());
    container.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
    container.setConcurrency(1);
    return container;
  }

  @Bean
  public Map<String, Object> ackModeManualConsumerConfig() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 1000);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
    return props;
  }
}
