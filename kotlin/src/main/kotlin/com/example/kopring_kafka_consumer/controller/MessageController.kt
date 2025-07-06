package com.example.kopring_kafka_consumer.controller

import com.example.kopring_kafka_consumer.service.KafkaProducerService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/kafka")
class MessageController(private val producerService: KafkaProducerService) {

    @PostMapping("/publish")
    fun sendMessageToKafka(@RequestBody message: String): String {
        producerService.sendMessage(message)
        return "Message sent to Kafka: $message"
    }
}
