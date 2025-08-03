package team.sipe.antcode97.consumerfetchmin1024.service

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong

/**
 * fetch.min.bytes = 1024 카프카 컨슈머 서비스
 * 
 * 실무에서 중요한 fetch.min.bytes 설정을 테스트합니다.
 * - fetch.min.bytes = 1024 (1KB)
 * - 네트워크 효율성과 지연의 균형
 * - 일반적인 웹 애플리케이션에 적합
 */
@Service
class FetchMin1024ConsumerService {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val messageCount = AtomicLong(0)
    private val startTime = System.currentTimeMillis()
    
    companion object {
        const val TOPIC_NAME = "uuid-messages-high-volume"
        const val GROUP_ID = "fetch-min-1024-consumer-group"
    }
    
    /**
     * fetch.min.bytes = 1024 설정으로 메시지 컨슘
     * 
     * 이 설정은 네트워크 효율성과 지연의 균형을 위해 사용됩니다.
     * - 최소 1KB가 쌓일 때까지 대기
     * - 일반적인 웹 애플리케이션에 적합
     * - 네트워크 호출 횟수 감소
     */
    @KafkaListener(
        topics = [TOPIC_NAME],
        groupId = GROUP_ID,
        containerFactory = "fetchMin1024KafkaListenerContainerFactory"
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
                    "[fetch.min.bytes=1024] 메시지 처리 - " +
                    "count={}, uuid={}, digitSum={}, " +
                    "topic={}, partition={}, offset={}, " +
                    "elapsed={}ms, rate={} msg/sec",
                    currentCount, uuid, digitSum, topic, partition, offset,
                    elapsedTime, String.format("%.2f", rate)
                )
                
                // 성능 통계 출력
                logger.info("=== fetch.min.bytes=1024 성능 통계 ===")
                logger.info("총 처리 메시지: {}", currentCount)
                logger.info("경과 시간: {}ms", elapsedTime)
                logger.info("처리 속도: {} msg/sec", String.format("%.2f", rate))
                logger.info("평균 처리 시간: {} ms/msg", String.format("%.2f", elapsedTime.toDouble() / currentCount))
                logger.info("설정: fetch.min.bytes=1024 (1KB) - 네트워크 효율성과 지연의 균형")
                logger.info("========================")
            }
            
            // 수동 커밋
            acknowledgment.acknowledge()
            
        } catch (e: Exception) {
            logger.error(
                "[fetch.min.bytes=1024] 메시지 처리 실패 - uuid={}, topic={}, partition={}, offset={}",
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
            "consumerType" to "Fetch Min 1024 Consumer",
            "totalMessages" to currentCount,
            "elapsedTimeMs" to elapsedTime,
            "rateMsgPerSec" to rate,
            "avgProcessingTimeMs" to if (currentCount > 0) elapsedTime.toDouble() / currentCount else 0.0,
            "fetchMinBytes" to 1024,
            "description" to "네트워크 효율성과 지연의 균형 - 일반적인 웹 애플리케이션에 적합"
        )
    }
} 