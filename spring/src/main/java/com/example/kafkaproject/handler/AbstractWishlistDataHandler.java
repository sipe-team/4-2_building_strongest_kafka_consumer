package com.example.kafkaproject.handler;

import com.example.kafkaproject.client.MarketingSolutionApiClient;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractWishlistDataHandler implements WishlistDataHandler {
  private final MarketingSolutionApiClient marketingApiClient;
  private final String supportedTopic;

  @Override
  public boolean supports(String topic) {
    return supportedTopic.equals(topic);
  }

  @Override
  public void process(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
    try {
      String value = record.value();
      String operation = value.contains("ADD") ? "ADD" : "REMOVE";

      // 1. 하위 클래스에 위임하여 attributes 생성 (Template Method)
      Map<String, Object> attributes = transformToAttribute(value);

      // 2. 마케팅 솔루션 API 전송 데이터 조립
      Map<String, Object> payload = buildPayload(operation, attributes);
      marketingApiClient.sendData(payload);
    } catch (Exception e) {
      log.error("Failed to process record: {}", record, e);
    } finally {
      acknowledgment.acknowledge();
    }
  }

  private Map<String, Object> buildPayload(String operation, Map<String, Object> attributes) {
    Map<String, Object> attributeWrapper = Map.of("attributes", attributes);
    if ("ADD".equals(operation)) {
      return Map.of("$add", attributeWrapper);
    } else if ("REMOVE".equals(operation)) {
      return Map.of("$remove", attributeWrapper);
    }
    throw new IllegalArgumentException("Unknown operation: " + operation);
  }

  protected abstract Map<String, Object> transformToAttribute(String value);
}
