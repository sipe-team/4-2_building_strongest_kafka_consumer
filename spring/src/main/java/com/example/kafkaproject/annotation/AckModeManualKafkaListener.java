package com.example.kafkaproject.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.kafka.annotation.KafkaListener;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@KafkaListener(containerFactory = "ackModeManualKafkaContainerFactory")
public @interface AckModeManualKafkaListener {
  @AliasFor(annotation = KafkaListener.class, attribute = "topics")
  String[] topics();

  @AliasFor(annotation = KafkaListener.class, attribute = "groupId")
  String groupId() default "";
}
