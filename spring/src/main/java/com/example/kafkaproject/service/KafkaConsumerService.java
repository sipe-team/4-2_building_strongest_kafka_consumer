package com.example.kafkaproject.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

  @KafkaListener(topics = "${spring.kafka.topic.message-process-topic}", groupId = "spring-group")
  public void consume(String message) {
    System.out.println("Consumed message: " + message);
  }
}
