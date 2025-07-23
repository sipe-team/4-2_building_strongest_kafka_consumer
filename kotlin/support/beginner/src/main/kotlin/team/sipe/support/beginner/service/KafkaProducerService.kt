package team.sipe.support.beginner.service

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class KafkaProducerService(private val kafkaTemplate: KafkaTemplate<String, String>) {

    private val TOPIC = "message-process-topic"

    fun sendMessage(message: String) {
        println("Producing message: $message to topic $TOPIC")
        kafkaTemplate.send(TOPIC, message)
    }

    fun sendMessage(topic: String, message: String) {
        println("Producing message: $message to topic $TOPIC")
        kafkaTemplate.send(topic, message + LocalDateTime.now(), message)
    }

}
