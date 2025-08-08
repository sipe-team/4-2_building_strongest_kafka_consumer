package team.sipe.antcode97.consumermaxpoll100.service

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong

/**
 * max.poll.records = 100 카프카 컨슈머 서비스
 * 
 * 실무에서 중요한 max.poll.records 설정을 테스트합니다.
 * - max.poll.records = 100 (소량 처리)
 * - 빠른 응답이 필요한 경우
 * - 메모리 사용량을 최소화하고 싶은 경우
 */
@Service
class MaxPoll100ConsumerService {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val messageCount = AtomicLong(0)
    private val startTime = System.currentTimeMillis()
    
    companion object {
        const val TOPIC_NAME = "uuid-messages-high-volume"
        const val GROUP_ID = "max-poll-100-consumer-group"
    }
    
    /**
     * max.poll.records = 100 설정으로 메시지 컨슘
     * 
     * 이 설정은 소량 처리를 위해 사용됩니다.
     * - 한 번에 최대 100개 레코드만 가져옴
     * - 빠른 응답이 필요한 경우
     * - 메모리 사용량을 최소화하고 싶은 경우
     */
    @KafkaListener(
        topics = [TOPIC_NAME],
        groupId = GROUP_ID,
        containerFactory = "maxPoll100KafkaListenerContainerFactory"
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
                    "[max.poll.records=100] 메시지 처리 - " +
                    "count={}, uuid={}, digitSum={}, " +
                    "topic={}, partition={}, offset={}, " +
                    "elapsed={}ms, rate={} msg/sec",
                    currentCount, uuid, digitSum, topic, partition, offset,
                    elapsedTime, String.format("%.2f", rate)
                )
                
                // 성능 통계 출력
                logger.info("=== max.poll.records=100 성능 통계 ===")
                logger.info("총 처리 메시지: {}", currentCount)
                logger.info("경과 시간: {}ms", elapsedTime)
                logger.info("처리 속도: {} msg/sec", String.format("%.2f", rate))
                logger.info("평균 처리 시간: {} ms/msg", String.format("%.2f", elapsedTime.toDouble() / currentCount))
                logger.info("설정: max.poll.records=100 (소량 처리) - 빠른 응답, 메모리 최소화")
                logger.info("========================")
            }
            
            // 수동 커밋
            acknowledgment.acknowledge()
            
        } catch (e: Exception) {
            logger.error(
                "[max.poll.records=100] 메시지 처리 실패 - uuid={}, topic={}, partition={}, offset={}",
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
            "consumerType" to "Max Poll 100 Consumer",
            "totalMessages" to currentCount,
            "elapsedTimeMs" to elapsedTime,
            "rateMsgPerSec" to rate,
            "avgProcessingTimeMs" to if (currentCount > 0) elapsedTime.toDouble() / currentCount else 0.0,
            "maxPollRecords" to 100,
            "description" to "소량 처리 - 빠른 응답, 메모리 최소화"
        )
    }
} 