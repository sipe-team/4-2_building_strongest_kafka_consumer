package team.sipe.antcode97.consumerultraoptimized

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Ultra Optimized 카프카 컨슈머 애플리케이션
 * 
 * 모든 최적화 설정을 조합한 최고 성능 컨슈머입니다.
 * - max.poll.records = 10000 (극대량 처리)
 * - fetch.min.bytes = 4096 (4KB, 네트워크 효율성 극대화)
 * - fetch.max.wait.ms = 50 (빠른 응답)
 * - session.timeout.ms = 60000 (안정적인 세션)
 * - heartbeat.interval.ms = 10000 (효율적인 하트비트)
 * - enable.auto.commit = false (수동 커밋)
 * - ack.mode = MANUAL_IMMEDIATE (즉시 커밋)
 */
@SpringBootApplication
class KafkaConsumerUltraOptimizedApplication

fun main(args: Array<String>) {
    runApplication<KafkaConsumerUltraOptimizedApplication>(*args)
} 