package com.example.kafkaproject.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RetryableInterface {
  String type(); // API, CONSUMER

  String interfaceName(); // API url, KAFKA topic

  String resource(); // API url, KAFKA topic

  String failureTopic(); // dlq name

  int maxRetries() default 3;

  String retryCountHeader() default "x-retry-count";
}
