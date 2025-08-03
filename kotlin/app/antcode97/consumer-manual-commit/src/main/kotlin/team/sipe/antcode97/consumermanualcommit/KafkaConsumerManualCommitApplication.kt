package team.sipe.antcode97.consumermanualcommit

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * 수동 커밋 카프카 컨슈머 애플리케이션
 * 
 * 카프카 핵심 가이드의 수동 커밋 전략을 구현합니다.
 * - 수동 커밋 (enable.auto.commit = false)
 * - 정확히 한 번 처리 보장
 * - UUID 자릿수 합계 계산
 */
@SpringBootApplication
class KafkaConsumerManualCommitApplication

fun main(args: Array<String>) {
    runApplication<KafkaConsumerManualCommitApplication>(*args)
} 