package com.example.kafkaproject.handler;

import com.example.kafkaproject.client.MarketingSolutionApiClient;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProductWishlistDataHandler extends AbstractWishlistDataHandler {

  public ProductWishlistDataHandler(
      MarketingSolutionApiClient marketingApiClient,
      @Value("${spring.kafka.topic.message-process-topic}") String supportedTopic) {
    super(marketingApiClient, supportedTopic);
  }

  @Override
  protected Map<String, Object> transformToAttribute(String value) {
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("product_id", value);
    return attributes;
  }
}
