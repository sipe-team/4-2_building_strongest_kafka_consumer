package com.example.kafkaproject.handler;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;

public interface WishlistDataHandler {
  boolean supports(String topic);

  void process(ConsumerRecord<String, String> record, Acknowledgment acknowledgment);
}
