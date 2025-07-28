package com.example.kafkaproject.factory;

import com.example.kafkaproject.handler.WishlistDataHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WishlistHandlerFactory {
  private final List<WishlistDataHandler> handlers;

  public WishlistDataHandler findHandler(String topic) {
    return handlers.stream()
        .filter(handler -> handler.supports(topic))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No handler found for topic: " + topic));
  }
}
