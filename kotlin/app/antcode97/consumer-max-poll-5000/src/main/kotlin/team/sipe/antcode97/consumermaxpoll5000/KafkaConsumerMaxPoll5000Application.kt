package team.sipe.antcode97.consumermaxpoll5000

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * max.poll.records = 5000 카프카 컨슈머 애플리케이션
 * 
 * 실무에서 중요한 max.poll.records 설정을 테스트합니다.
 * - max.poll.records = 5000 (대량 처리)
 * - 높은 처리량이 필요한 경우
 * - 배치 처리가 중요한 경우
 */
@SpringBootApplication
class KafkaConsumerMaxPoll5000Application

fun main(args: Array<String>) {
    runApplication<KafkaConsumerMaxPoll5000Application>(*args)
} 