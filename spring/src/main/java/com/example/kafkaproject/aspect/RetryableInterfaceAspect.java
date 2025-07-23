package com.example.kafkaproject.aspect;

import com.example.kafkaproject.annotation.RetryableInterface;
import com.example.kafkaproject.model.InterfaceFailurePayload;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RetryableInterfaceAspect {
  private final KafkaTemplate<String, Object> kafkaTemplate;

  @Pointcut("@annotation(com.example.kafkaproject.annotation.RetryableInterface)")
  public void retryableInterfacePointcut() {}

  @AfterThrowing(pointcut = "retryableInterfacePointcut()", throwing = "ex")
  public void handleFailure(JoinPoint joinPoint, Throwable ex) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    RetryableInterface annotation = signature.getMethod().getAnnotation(RetryableInterface.class);

    String interfaceName = annotation.interfaceName();
    String failureTopic = annotation.failureTopic();
    String type = annotation.type();
    String resource = annotation.resource();

    if ("CONSUMER".equals(type)) {
      ConsumerRecord<?, ?> consumerRecord = findConsumerRecord(joinPoint.getArgs());
      kafkaTemplate.send(
          failureTopic,
          (String) consumerRecord.key(),
          InterfaceFailurePayload.of(type, resource, interfaceName, consumerRecord));
    } else {
      Map<String, Object> arguments = findApiJoinPoint(joinPoint);
      kafkaTemplate.send(
          failureTopic,
          arguments.toString(),
          InterfaceFailurePayload.of(type, resource, interfaceName, arguments));
    }
  }

  private Map<String, Object> findApiJoinPoint(JoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String[] parameterNames = signature.getParameterNames();
    Map<String, Object> arguments = new LinkedHashMap<>();
    Object[] args = joinPoint.getArgs();
    for (int i = 0; i < args.length; i++) {
      String paramName =
          (parameterNames != null && parameterNames.length > i) ? parameterNames[i] : "arg" + i;
      arguments.put(paramName, args[i]);
    }
    return arguments;
  }

  private ConsumerRecord<?, ?> findConsumerRecord(Object[] args) {
    if (args == null) return null;
    return Arrays.stream(args)
        .filter(ConsumerRecord.class::isInstance)
        .map(ConsumerRecord.class::cast)
        .findFirst()
        .orElse(null);
  }
}
