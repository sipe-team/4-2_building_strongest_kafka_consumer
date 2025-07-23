package com.example.kafkaproject.model;

import lombok.Builder;
import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerRecord;

@Getter
@Builder
public class InterfaceFailurePayload {
  private String interfaceName;
  private String resource;
  private Object payload;
  private String type;

  public static InterfaceFailurePayload of(String type, String resource, String interfaceName, Object consumerRecord) {
    return InterfaceFailurePayload.builder()
            .interfaceName(interfaceName)
            .resource(resource)
            .payload(consumerRecord)
            .type(type)
            .build();
  }
}
