package com.example.kafkaproject.controller;

import com.example.kafkaproject.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class MessageController {
    @Value("${spring.kafka.topic.message-process-topic}")
    private String topic;

    private final KafkaProducerService producerService;

    public MessageController(KafkaProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping("/send")
    public String sendMessage(@RequestBody String message) {
        producerService.sendMessage(topic, message);
        return "Message sent: " + message;
    }
}
