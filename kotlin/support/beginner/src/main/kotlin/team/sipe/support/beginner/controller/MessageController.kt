package team.sipe.support.beginner.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import team.sipe.support.beginner.service.KafkaProducerService


@RestController
@RequestMapping("/kafka")
class MessageController(private val producerService: KafkaProducerService) {

    @PostMapping("/publish")
    fun sendMessageToKafka(@RequestBody message: String): String {
        producerService.sendMessage(message)
        return "Message sent to Kafka: $message"
    }
}
