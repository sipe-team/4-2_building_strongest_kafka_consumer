package team.sipe.antcode97.producer.service

import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicLong

/**
 * UUID 메시지 프로듀서 서비스
 * 
 * 실무에서 사용하는 대량 메시지 발행과 성능 테스트를 위한 프로듀서입니다.
 * - 대량 메시지 발행 (100만개 이상)
 * - 다양한 배치 크기 테스트
 * - 실시간 성능 모니터링
 */
@Service
class UuidProducerService(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val messageCount = AtomicLong(0)
    
    companion object {
        const val TOPIC_NAME = "uuid-messages"
        const val BATCH_TOPIC_NAME = "uuid-messages-batch"
        const val HIGH_VOLUME_TOPIC = "uuid-messages-high-volume"
    }
    
    /**
     * 단일 UUID 메시지를 동기적으로 발행
     * 
     * @return 발행된 메시지의 UUID
     */
    fun sendSingleMessage(): String {
        val uuid = UUID.randomUUID().toString()
        
        return try {
            val result = kafkaTemplate.send(TOPIC_NAME, uuid).get()
            val count = messageCount.incrementAndGet()
            logger.info("메시지 발행 성공: count={}, partition={}, offset={}, uuid={}", 
                count, result.recordMetadata.partition(), 
                result.recordMetadata.offset(), uuid)
            uuid
        } catch (e: Exception) {
            logger.error("메시지 발행 실패: uuid={}", uuid, e)
            throw e
        }
    }
    
    /**
     * 단일 UUID 메시지를 비동기적으로 발행
     * 
     * @return CompletableFuture<SendResult>
     */
    fun sendMessageAsync(): CompletableFuture<SendResult<String, String>> {
        val uuid = UUID.randomUUID().toString()
        
        return kafkaTemplate.send(TOPIC_NAME, uuid)
            .whenComplete { result, throwable ->
                if (throwable != null) {
                    logger.error("비동기 메시지 발행 실패: uuid={}", uuid, throwable)
                } else {
                    val count = messageCount.incrementAndGet()
                    logger.info("비동기 메시지 발행 성공: count={}, partition={}, offset={}, uuid={}", 
                        count, result.recordMetadata.partition(), 
                        result.recordMetadata.offset(), uuid)
                }
            }
    }
    
    /**
     * 배치로 여러 UUID 메시지를 발행
     * 
     * @param count 발행할 메시지 개수
     * @return 발행된 UUID 리스트
     */
    fun sendBatchMessages(count: Int): List<String> {
        val uuids = (1..count).map { UUID.randomUUID().toString() }
        
        return uuids.map { uuid ->
            try {
                kafkaTemplate.send(BATCH_TOPIC_NAME, uuid).get()
                uuid
            } catch (e: Exception) {
                logger.error("배치 메시지 발행 실패: uuid={}", uuid, e)
                throw e
            }
        }
    }
    
    /**
     * 실무 대용량 메시지 발행 (100만개 이상)
     * 
     * @param count 발행할 메시지 개수 (기본: 1,000,000개)
     * @param batchSize 배치 크기 (기본: 10,000개)
     * @param reportInterval 성능 리포트 간격 (기본: 10,000개마다)
     * @param topic 발행할 토픽 (기본: HIGH_VOLUME_TOPIC)
     */
    fun sendMassiveMessages(count: Long = 1_000_000, batchSize: Int = 10_000, reportInterval: Int = 10_000, topic: String = HIGH_VOLUME_TOPIC) {
        logger.info("🚀 대용량 메시지 발행 시작: 총 {}개, 배치 크기: {}, 토픽: {}", count, batchSize, topic)
        
        val startTime = System.currentTimeMillis()
        val totalBatches = (count / batchSize).toInt()
        
        (0 until totalBatches).forEach { batchIndex ->
            val batchStart = batchIndex * batchSize
            val batchEnd = minOf(batchStart + batchSize.toLong(), count)
            val currentBatchSize = (batchEnd - batchStart).toInt()
            
            // 배치 메시지 생성
            val batch = (0 until currentBatchSize).map { UUID.randomUUID().toString() }
            
            // 비동기로 배치 발행
            val futures = batch.map { uuid ->
                kafkaTemplate.send(topic, uuid)
            }
            
            // 배치 완료 대기
            CompletableFuture.allOf(*futures.toTypedArray()).join()
            
            val currentCount = messageCount.addAndGet(currentBatchSize.toLong())
            
            // 성능 리포트 (reportInterval마다)
            if (currentCount % reportInterval == 0L) {
                val elapsedTime = System.currentTimeMillis() - startTime
                val rate = currentCount * 1000.0 / elapsedTime
                val progress = (currentCount * 100.0 / count).toInt()
                
                logger.info("📊 발행 진행률: {}/{} ({}%) - 속도: {:.2f} msg/sec - 경과: {}ms", 
                    currentCount, count, progress, rate, elapsedTime)
            }
        }
        
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        val rate = count * 1000.0 / duration
        
        logger.info("✅ 대용량 메시지 발행 완료!")
        logger.info("📈 최종 통계:")
        logger.info("   - 총 발행 메시지: {}개", count)
        logger.info("   - 총 소요 시간: {}ms ({}초)", duration, duration / 1000.0)
        logger.info("   - 평균 발행 속도: {:.2f} msg/sec", rate)
        logger.info("   - 배치 크기: {}개", batchSize)
        logger.info("   - 총 배치 수: {}개", totalBatches)
        logger.info("   - 토픽: {}", topic)
    }
    
    /**
     * 다양한 배치 크기로 성능 테스트
     * 
     * @param totalCount 총 메시지 개수
     * @param batchSizes 테스트할 배치 크기들
     */
    fun performanceTestWithDifferentBatchSizes(totalCount: Long = 100_000, batchSizes: List<Int> = listOf(100, 1000, 10000, 50000)) {
        logger.info("🧪 다양한 배치 크기 성능 테스트 시작: 총 {}개 메시지", totalCount)
        
        batchSizes.forEach { batchSize ->
            logger.info("📊 배치 크기 {}개로 테스트 시작...", batchSize)
            
            val startTime = System.currentTimeMillis()
            val totalBatches = (totalCount / batchSize).toInt()
            
            (0 until totalBatches).forEach { batchIndex ->
                val batchStart = batchIndex * batchSize
                val batchEnd = minOf(batchStart + batchSize.toLong(), totalCount)
                val currentBatchSize = (batchEnd - batchStart).toInt()
                
                val batch = (0 until currentBatchSize).map { UUID.randomUUID().toString() }
                val futures = batch.map { uuid ->
                    kafkaTemplate.send(HIGH_VOLUME_TOPIC, uuid)
                }
                
                CompletableFuture.allOf(*futures.toTypedArray()).join()
            }
            
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime
            val rate = totalCount * 1000.0 / duration
            
            logger.info("📈 배치 크기 {}개 테스트 완료:", batchSize)
            logger.info("   - 소요 시간: {}ms", duration)
            logger.info("   - 처리 속도: {} msg/sec", String.format("%.2f", rate))
            logger.info("   - 배치 수: {}개", totalBatches)
            logger.info("   - 평균 배치 처리 시간: {} ms/batch", String.format("%.2f", duration.toDouble() / totalBatches))
        }
    }
    
    /**
     * 지속적인 메시지 발행 (스트레스 테스트용)
     * 
     * @param durationSeconds 지속 시간 (초)
     * @param ratePerSecond 초당 발행할 메시지 수
     */
    fun sendContinuousMessages(durationSeconds: Int = 60, ratePerSecond: Int = 1000) {
        logger.info("🔄 지속적 메시지 발행 시작: {}초간 초당 {}개", durationSeconds, ratePerSecond)
        
        val startTime = System.currentTimeMillis()
        val endTime = startTime + (durationSeconds * 1000L)
        // val intervalMs = 1000L / ratePerSecond  // 현재 사용하지 않음
        
        var messageCount = 0L
        
        while (System.currentTimeMillis() < endTime) {
            val batchStart = System.currentTimeMillis()
            
            // ratePerSecond만큼 메시지 발행
            val futures = (0 until ratePerSecond).map {
                kafkaTemplate.send(HIGH_VOLUME_TOPIC, UUID.randomUUID().toString())
            }
            
            CompletableFuture.allOf(*futures.toTypedArray()).join()
            
            messageCount += ratePerSecond
            
            val batchEnd = System.currentTimeMillis()
            val batchDuration = batchEnd - batchStart
            
            // 다음 배치까지 대기
            if (batchDuration < 1000) {
                Thread.sleep(1000 - batchDuration)
            }
            
            val elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000
            logger.info("📊 지속 발행 진행: {}초 경과, 총 {}개 발행", elapsedSeconds, messageCount)
        }
        
        val totalDuration = System.currentTimeMillis() - startTime
        val actualRate = messageCount * 1000.0 / totalDuration
        
        logger.info("✅ 지속적 메시지 발행 완료!")
        logger.info("📈 최종 통계:")
        logger.info("   - 총 발행 메시지: {}개", messageCount)
        logger.info("   - 총 소요 시간: {}ms", totalDuration)
        logger.info("   - 실제 발행 속도: {} msg/sec", String.format("%.2f", actualRate))
        logger.info("   - 목표 발행 속도: {} msg/sec", ratePerSecond)
    }
    
    /**
     * 현재 발행 통계 조회
     */
    fun getStatistics(): Map<String, Any> {
        return mapOf(
            "totalMessagesSent" to messageCount.get(),
            "topics" to listOf(TOPIC_NAME, BATCH_TOPIC_NAME, HIGH_VOLUME_TOPIC)
        )
    }
} 