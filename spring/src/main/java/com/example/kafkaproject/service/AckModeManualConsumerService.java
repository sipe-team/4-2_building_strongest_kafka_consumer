package com.example.kafkaproject.service;

import com.example.kafkaproject.annotation.AckModeManualKafkaListener;
import com.example.kafkaproject.factory.WishlistHandlerFactory;
import com.example.kafkaproject.handler.WishlistDataHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AckModeManualConsumerService implements AcknowledgingMessageListener<String, String> {
  private final WishlistHandlerFactory handlerFactory;

  @Override
  @AckModeManualKafkaListener(topics = "message-process-topic", groupId = "ack-mode-consumer")
  public void onMessage(ConsumerRecord<String, String> data, Acknowledgment acknowledgment) {
    log.info("Received message: {}", data);
    WishlistDataHandler handler = handlerFactory.findHandler(data.topic());
    handler.process(data, acknowledgment);
  }
}
