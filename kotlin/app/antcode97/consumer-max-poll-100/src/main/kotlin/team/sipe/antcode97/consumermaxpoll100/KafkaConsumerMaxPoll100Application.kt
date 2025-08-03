package team.sipe.antcode97.consumermaxpoll100

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * max.poll.records = 100 카프카 컨슈머 애플리케이션
 * 
 * 실무에서 중요한 max.poll.records 설정을 테스트합니다.
 * - max.poll.records = 100 (소량 처리)
 * - 빠른 응답이 필요한 경우
 * - 메모리 사용량을 최소화하고 싶은 경우
 */
@SpringBootApplication
class KafkaConsumerMaxPoll100Application

fun main(args: Array<String>) {
    runApplication<KafkaConsumerMaxPoll100Application>(*args)
} 