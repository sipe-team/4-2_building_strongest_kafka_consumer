package com.example.kafkaproject.client;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarketingSolutionApiClient {

  public void sendData(Map<String, Object> payload) {
    System.out.println(payload);
  }
}
