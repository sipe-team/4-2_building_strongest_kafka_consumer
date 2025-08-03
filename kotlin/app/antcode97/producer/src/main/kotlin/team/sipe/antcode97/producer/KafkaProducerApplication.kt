package team.sipe.antcode97.producer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * 카프카 프로듀서 애플리케이션
 * 
 * UUID를 생성하여 메시지로 발행하는 프로듀서입니다.
 * 각 컨슈머 전략별로 성능 테스트를 위한 메시지를 생성합니다.
 */
@SpringBootApplication
class KafkaProducerApplication

fun main(args: Array<String>) {
    runApplication<KafkaProducerApplication>(*args)
} 