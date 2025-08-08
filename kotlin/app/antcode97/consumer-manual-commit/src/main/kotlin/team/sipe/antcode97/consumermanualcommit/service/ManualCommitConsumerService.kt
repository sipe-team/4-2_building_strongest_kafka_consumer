package team.sipe.antcode97.consumermanualcommit.service

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong

/**
 * 수동 커밋 카프카 컨슈머 서비스
 * 
 * 카프카 핵심 가이드 p.150-170의 수동 커밋 전략을 구현합니다.
 * - 수동 커밋 (enable.auto.commit = false)
 * - 정확히 한 번 처리 보장
 * - UUID 자릿수 합계 계산
 */
@Service
class ManualCommitConsumerService {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val messageCount = AtomicLong(0)
    private val startTime = System.currentTimeMillis()
    
    companion object {
        const val TOPIC_NAME = "uuid-messages-high-volume"
        const val GROUP_ID = "manual-commit-consumer-group"
    }
    
    /**
     * 수동 커밋 UUID 메시지 컨슘
     * 
     * 카프카 핵심 가이드 p.155의 수동 커밋 설정을 사용합니다.
     * - enable.auto.commit = false
     * - ackMode = MANUAL_IMMEDIATE
     * - 정확히 한 번 처리 보장
     */
    @KafkaListener(
        topics = [TOPIC_NAME],
        groupId = GROUP_ID,
        containerFactory = "manualCommitKafkaListenerContainerFactory"
    )
    fun consumeUuidMessage(
        @Payload uuid: String,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partition: Int,
        @Header(KafkaHeaders.OFFSET) offset: Long,
        acknowledgment: Acknowledgment
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
                "[수동커밋컨슈머] 메시지 처리 완료 - " +
                "count={}, uuid={}, digitSum={}, " +
                "topic={}, partition={}, offset={}, " +
                "elapsed={}ms, rate={} msg/sec",
                currentCount, uuid, digitSum, topic, partition, offset,
                elapsedTime, String.format("%.2f", (currentCount * 1000.0 / elapsedTime))
            )
            
            // 성능 통계 출력 (1000개마다)
            if (currentCount % 1000 == 0L) {
                val rate = currentCount * 1000.0 / elapsedTime
                logger.info("=== 수동커밋컨슈머 성능 통계 ===")
                logger.info("총 처리 메시지: {}", currentCount)
                logger.info("경과 시간: {}ms", elapsedTime)
                logger.info("처리 속도: {} msg/sec", String.format("%.2f", rate))
                logger.info("평균 처리 시간: {} ms/msg", String.format("%.2f", elapsedTime.toDouble() / currentCount))
                logger.info("========================")
            }
            
            // 수동 커밋 - 카프카 핵심 가이드 p.160
            // 메시지 처리가 완료된 후에만 커밋
            acknowledgment.acknowledge()
            
        } catch (e: Exception) {
            logger.error(
                "[수동커밋컨슈머] 메시지 처리 실패 - uuid={}, topic={}, partition={}, offset={}",
                uuid, topic, partition, offset, e
            )
            
            // 에러 발생 시 커밋하지 않음
            // 카프카 핵심 가이드 p.165 - 재처리를 위해 커밋하지 않음
            // acknowledgment.acknowledge() 호출하지 않음
        }
    }
    
    /**
     * 배치 토픽 메시지 컨슘 (수동 커밋)
     */
    @KafkaListener(
        topics = ["uuid-messages-batch"],
        groupId = "$GROUP_ID-batch",
        containerFactory = "manualCommitKafkaListenerContainerFactory"
    )
    fun consumeBatchMessage(
        @Payload uuid: String,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partition: Int,
        @Header(KafkaHeaders.OFFSET) offset: Long,
        acknowledgment: Acknowledgment
    ) {
        val currentCount = messageCount.incrementAndGet()
        
        try {
            val digitSum = uuid.replace("-", "")
                .filter { it.isDigit() }
                .sumOf { it.toString().toInt() }
            
            logger.info(
                "[수동커밋컨슈머-배치] 메시지 처리 완료 - " +
                "count={}, uuid={}, digitSum={}, " +
                "topic={}, partition={}, offset={}",
                currentCount, uuid, digitSum, topic, partition, offset
            )
            
            // 수동 커밋
            acknowledgment.acknowledge()
            
        } catch (e: Exception) {
            logger.error(
                "[수동커밋컨슈머-배치] 메시지 처리 실패 - uuid={}, topic={}, partition={}, offset={}",
                uuid, topic, partition, offset, e
            )
            // 에러 발생 시 커밋하지 않음
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
            "consumerType" to "Manual Commit Consumer",
            "totalMessages" to currentCount,
            "elapsedTimeMs" to elapsedTime,
            "rateMsgPerSec" to rate,
            "avgProcessingTimeMs" to if (currentCount > 0) elapsedTime.toDouble() / currentCount else 0.0,
            "commitStrategy" to "MANUAL_IMMEDIATE"
        )
    }
} 