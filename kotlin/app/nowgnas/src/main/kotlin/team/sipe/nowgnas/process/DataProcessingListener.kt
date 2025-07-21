package team.sipe.nowgnas.process

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Service

@Service
class DataProcessingListener {
    @KafkaListener(
        topics = ["\${spring.kafka.topic.consumer-practice}"],
        containerFactory = "ackModeManualKafkaContainerFactory"
    )
    fun consume(record: ConsumerRecord<String, String>, ack: Acknowledgment) {
        try {
            println("Consumed record: $record")
        } catch (e: Exception) {
            println("Error consuming record: $record")
        } finally {
            ack.acknowledge()
        }
    }
}