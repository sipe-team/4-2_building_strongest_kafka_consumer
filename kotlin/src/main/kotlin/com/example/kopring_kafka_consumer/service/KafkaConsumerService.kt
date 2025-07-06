package com.example.kopring_kafka_consumer.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class KafkaConsumerService {

    @KafkaListener(topics = ["\${spring.kafka.topic.message-process-topic}"], groupId = "my-group-id")
    fun consume(message: String) {
        println("Consumed message: $message")
    }
}
