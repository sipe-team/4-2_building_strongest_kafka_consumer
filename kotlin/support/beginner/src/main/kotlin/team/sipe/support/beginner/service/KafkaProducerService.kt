package team.sipe.support.beginner.service

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaProducerService(private val kafkaTemplate: KafkaTemplate<String, String>) {

    private val TOPIC = "message-process-topic"

    fun sendMessage(message: String) {
        println("Producing message: $message to topic $TOPIC")
        kafkaTemplate.send(TOPIC, message)
    }
}
