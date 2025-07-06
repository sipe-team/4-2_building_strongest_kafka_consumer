package team.sipe.antcode97

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import team.sipe.support.beginner.EnableBeginner

@EnableBeginner
@SpringBootApplication
class KafkaConsumerApplication

fun main(args: Array<String>) {
    runApplication<KafkaConsumerApplication>(*args)
}