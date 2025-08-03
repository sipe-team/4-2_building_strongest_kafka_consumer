package team.sipe.antcode97.consumerbasic

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * 기본 카프카 컨슈머 애플리케이션
 * 
 * 카프카 핵심 가이드의 기본 컨슈머 전략을 구현합니다.
 * - 자동 커밋
 * - 단순한 메시지 처리
 * - UUID 자릿수 합계 계산
 */
@SpringBootApplication
class KafkaConsumerBasicApplication

fun main(args: Array<String>) {
    runApplication<KafkaConsumerBasicApplication>(*args)
} 