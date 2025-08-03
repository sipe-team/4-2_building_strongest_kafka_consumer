package team.sipe.antcode97.consumerbasic.service

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong

/**
 * 기본 카프카 컨슈머 서비스
 * 
 * 카프카 핵심 가이드 p.120-140의 기본 컨슈머 전략을 구현합니다.
 * - 자동 커밋 (enable.auto.commit = true)
 * - 단순한 메시지 처리
 * - UUID 자릿수 합계 계산
 */
@Service
class BasicConsumerService {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val messageCount = AtomicLong(0)
    private val startTime = System.currentTimeMillis()
    
    companion object {
        const val TOPIC_NAME = "uuid-messages-high-volume"
        const val GROUP_ID = "basic-consumer-group"
    }
    
    /**
     * 기본 UUID 메시지 컨슘
     * 
     * 카프카 핵심 가이드 p.125의 기본 컨슈머 설정을 사용합니다.
     * - auto.offset.reset = earliest
     * - enable.auto.commit = true
     * - auto.commit.interval.ms = 5000
     */
    @KafkaListener(
        topics = [TOPIC_NAME],
        groupId = GROUP_ID,
        containerFactory = "basicKafkaListenerContainerFactory"
    )
    fun consumeUuidMessage(
        @Payload uuid: String,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partition: Int,
        @Header(KafkaHeaders.OFFSET) offset: Long
    ) {
        val currentCount = messageCount.incrementAndGet()
        val elapsedTime = System.currentTimeMillis() - startTime
        
        try {
            // UUID에서 숫자만 추출하여 자릿수 합계 계산
            val digitSum = uuid.replace("-", "")
                .filter { it.isDigit() }
                .sumOf { it.toString().toInt() }
            
            // 처리 결과 로깅
            logger.info(
                "[기본컨슈머] 메시지 처리 완료 - " +
                "count={}, uuid={}, digitSum={}, " +
                "topic={}, partition={}, offset={}, " +
                "elapsed={}ms, rate={} msg/sec",
                currentCount, uuid, digitSum, topic, partition, offset,
                elapsedTime, String.format("%.2f", (currentCount * 1000.0 / elapsedTime))
            )
            
            // 성능 통계 출력 (1000개마다)
            if (currentCount % 1000 == 0L) {
                val rate = currentCount * 1000.0 / elapsedTime
                logger.info("=== 기본컨슈머 성능 통계 ===")
                logger.info("총 처리 메시지: {}", currentCount)
                logger.info("경과 시간: {}ms", elapsedTime)
                logger.info("처리 속도: {} msg/sec", String.format("%.2f", rate))
                logger.info("평균 처리 시간: {} ms/msg", String.format("%.2f", elapsedTime.toDouble() / currentCount))
                logger.info("========================")
            }
            
        } catch (e: Exception) {
            logger.error(
                "[기본컨슈머] 메시지 처리 실패 - uuid={}, topic={}, partition={}, offset={}",
                uuid, topic, partition, offset, e
            )
            // 기본 컨슈머는 예외 발생 시 자동으로 재시도하지 않음
            // 카프카 핵심 가이드 p.130 참조
        }
    }
    
    /**
     * 배치 토픽 메시지 컨슘
     */
    @KafkaListener(
        topics = ["uuid-messages-batch"],
        groupId = "$GROUP_ID-batch",
        containerFactory = "basicKafkaListenerContainerFactory"
    )
    fun consumeBatchMessage(
        @Payload uuid: String,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partition: Int,
        @Header(KafkaHeaders.OFFSET) offset: Long
    ) {
        val currentCount = messageCount.incrementAndGet()
        
        try {
            val digitSum = uuid.replace("-", "")
                .filter { it.isDigit() }
                .sumOf { it.toString().toInt() }
            
            logger.info(
                "[기본컨슈머-배치] 메시지 처리 완료 - " +
                "count={}, uuid={}, digitSum={}, " +
                "topic={}, partition={}, offset={}",
                currentCount, uuid, digitSum, topic, partition, offset
            )
            
        } catch (e: Exception) {
            logger.error(
                "[기본컨슈머-배치] 메시지 처리 실패 - uuid={}, topic={}, partition={}, offset={}",
                uuid, topic, partition, offset, e
            )
        }
    }
    
    /**
     * 현재 처리 통계 조회
     */
    fun getStatistics(): Map<String, Any> {
        val currentCount = messageCount.get()
        val elapsedTime = System.currentTimeMillis() - startTime
        val rate = if (elapsedTime > 0) currentCount * 1000.0 / elapsedTime else 0.0
        
        return mapOf(
            "consumerType" to "Basic Consumer",
            "totalMessages" to currentCount,
            "elapsedTimeMs" to elapsedTime,
            "rateMsgPerSec" to rate,
            "avgProcessingTimeMs" to if (currentCount > 0) elapsedTime.toDouble() / currentCount else 0.0
        )
    }
} 