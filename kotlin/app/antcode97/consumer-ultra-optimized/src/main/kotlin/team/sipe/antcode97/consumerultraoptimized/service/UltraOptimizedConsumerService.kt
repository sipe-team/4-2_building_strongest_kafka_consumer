package team.sipe.antcode97.consumerultraoptimized.service

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong

/**
 * Ultra Optimized 카프카 컨슈머 서비스
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
@Service
class UltraOptimizedConsumerService {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val messageCount = AtomicLong(0)
    private val startTime = System.currentTimeMillis()
    
    companion object {
        const val TOPIC_NAME = "uuid-messages-high-volume"
        const val GROUP_ID = "ultra-optimized-consumer-group"
    }
    
    /**
     * Ultra Optimized 설정으로 메시지 컨슘
     * 
     * 모든 최적화 설정을 조합한 최고 성능 설정입니다.
     * - 한 번에 최대 10000개 레코드를 가져옴
     * - 네트워크 효율성 극대화
     * - 빠른 응답과 높은 처리량의 균형
     */
    @KafkaListener(
        topics = [TOPIC_NAME],
        groupId = GROUP_ID,
        containerFactory = "ultraOptimizedKafkaListenerContainerFactory"
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
            
            // 처리 결과 로깅 (1000개마다)
            if (currentCount % 1000 == 0L) {
                val rate = currentCount * 1000.0 / elapsedTime
                logger.info(
                    "[Ultra Optimized] 메시지 처리 - " +
                            "count={}, uuid={}, digitSum={}, " +
                            "topic={}, partition={}, offset={}, " +
                            "elapsed={}ms, rate={} msg/sec",
                    currentCount, uuid, digitSum, topic, partition, offset,
                    elapsedTime, String.format("%.2f", rate)
                )
                
                // 성능 통계 출력
                logger.info("=== Ultra Optimized 성능 통계 ===")
                logger.info("총 처리 메시지: {}", currentCount)
                logger.info("경과 시간: {}ms", elapsedTime)
                logger.info("처리 속도: {} msg/sec", String.format("%.2f", rate))
                logger.info("평균 처리 시간: {} ms/msg", String.format("%.2f", elapsedTime.toDouble() / currentCount))
                logger.info("설정: max.poll.records=10000, fetch.min.bytes=4096, fetch.max.wait.ms=50")
                logger.info("설정: session.timeout.ms=60000, heartbeat.interval.ms=10000")
                logger.info("설정: enable.auto.commit=false, ack.mode=MANUAL_IMMEDIATE")
                logger.info("설정: StickyAssignor, 극대량 처리 + 네트워크 효율성 극대화")
                logger.info("========================")
            }
            
            // 수동 커밋 (즉시 커밋)
            acknowledgment.acknowledge()
            
        } catch (e: Exception) {
            logger.error(
                "[Ultra Optimized] 메시지 처리 실패 - uuid={}, topic={}, partition={}, offset={}",
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
            "consumerType" to "Ultra Optimized Consumer",
            "totalMessages" to currentCount,
            "elapsedTimeMs" to elapsedTime,
            "rateMsgPerSec" to rate,
            "avgProcessingTimeMs" to if (currentCount > 0) elapsedTime.toDouble() / currentCount else 0.0,
            "maxPollRecords" to 10000,
            "fetchMinBytes" to 4096,
            "fetchMaxWaitMs" to 50,
            "sessionTimeoutMs" to 60000,
            "heartbeatIntervalMs" to 10000,
            "description" to "극대량 처리 + 네트워크 효율성 극대화 + 빠른 응답"
        )
    }
} 