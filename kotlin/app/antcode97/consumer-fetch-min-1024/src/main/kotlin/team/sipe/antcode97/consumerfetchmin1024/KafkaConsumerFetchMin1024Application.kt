package team.sipe.antcode97.consumerfetchmin1024

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * fetch.min.bytes = 1024 카프카 컨슈머 애플리케이션
 * 
 * 실무에서 중요한 fetch.min.bytes 설정을 테스트합니다.
 * - fetch.min.bytes = 1024 (1KB)
 * - 네트워크 효율성과 지연의 균형
 * - 일반적인 웹 애플리케이션에 적합
 */
@SpringBootApplication
class KafkaConsumerFetchMin1024Application

fun main(args: Array<String>) {
    runApplication<KafkaConsumerFetchMin1024Application>(*args)
} 