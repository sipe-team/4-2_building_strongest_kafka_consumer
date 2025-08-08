package team.sipe.antcode97.producer.controller

import org.springframework.web.bind.annotation.*
import team.sipe.antcode97.producer.service.UuidProducerService
import java.util.concurrent.CompletableFuture

/**
 * 프로듀서 제어 API
 * 
 * 실무에서 사용하는 대량 메시지 발행과 성능 테스트를 위한 REST API를 제공합니다.
 */
@RestController
@RequestMapping("/api/producer")
class ProducerController(
    private val uuidProducerService: UuidProducerService
) {
    
    /**
     * 단일 메시지 발행 (동기)
     */
    @PostMapping("/send-single")
    fun sendSingleMessage(): Map<String, String> {
        val uuid = uuidProducerService.sendSingleMessage()
        return mapOf(
            "status" to "success",
            "uuid" to uuid,
            "message" to "단일 메시지가 성공적으로 발행되었습니다."
        )
    }
    
    /**
     * 단일 메시지 발행 (비동기)
     */
    @PostMapping("/send-async")
    fun sendAsyncMessage(): CompletableFuture<Map<String, String>> {
        return uuidProducerService.sendMessageAsync()
            .thenApply { result ->
                mapOf(
                    "status" to "success",
                    "partition" to result.recordMetadata.partition().toString(),
                    "offset" to result.recordMetadata.offset().toString(),
                    "message" to "비동기 메시지가 성공적으로 발행되었습니다."
                )
            }
            .exceptionally { throwable ->
                mapOf(
                    "status" to "error",
                    "message" to "메시지 발행 실패: ${throwable.message}"
                )
            }
    }
    
    /**
     * 배치 메시지 발행
     */
    @PostMapping("/send-batch")
    fun sendBatchMessages(@RequestParam(defaultValue = "100") count: Int): Map<String, Any> {
        val uuids = uuidProducerService.sendBatchMessages(count)
        return mapOf(
            "status" to "success",
            "count" to uuids.size,
            "uuids" to uuids.take(10), // 처음 10개만 반환
            "message" to "${count}개의 배치 메시지가 성공적으로 발행되었습니다."
        )
    }
    
    /**
     * 실무 대용량 메시지 발행 (100만개 이상)
     */
    @PostMapping("/send-massive")
    fun sendMassiveMessages(
        @RequestParam(defaultValue = "1000000") count: Long,
        @RequestParam(defaultValue = "10000") batchSize: Int,
        @RequestParam(defaultValue = "10000") reportInterval: Int,
        @RequestParam(defaultValue = "uuid-messages-high-volume") topic: String
    ): Map<String, Any> {
        // 비동기로 실행하여 즉시 응답
        CompletableFuture.runAsync {
            uuidProducerService.sendMassiveMessages(count, batchSize, reportInterval, topic)
        }
        
        return mapOf(
            "status" to "started",
            "count" to count,
            "batchSize" to batchSize,
            "reportInterval" to reportInterval,
            "topic" to topic,
            "message" to "대용량 메시지 발행이 시작되었습니다. 로그를 확인하세요."
        )
    }
    
    /**
     * 다양한 배치 크기로 성능 테스트
     */
    @PostMapping("/performance-test")
    fun performanceTestWithDifferentBatchSizes(
        @RequestParam(defaultValue = "100000") totalCount: Long,
        @RequestParam(defaultValue = "100,1000,10000,50000") batchSizes: String
    ): Map<String, Any> {
        val batchSizeList = batchSizes.split(",").map { it.trim().toInt() }
        
        // 비동기로 실행
        CompletableFuture.runAsync {
            uuidProducerService.performanceTestWithDifferentBatchSizes(totalCount, batchSizeList)
        }
        
        return mapOf(
            "status" to "started",
            "totalCount" to totalCount,
            "batchSizes" to batchSizeList,
            "message" to "다양한 배치 크기 성능 테스트가 시작되었습니다. 로그를 확인하세요."
        )
    }
    
    /**
     * 지속적인 메시지 발행 (스트레스 테스트)
     */
    @PostMapping("/send-continuous")
    fun sendContinuousMessages(
        @RequestParam(defaultValue = "60") durationSeconds: Int,
        @RequestParam(defaultValue = "1000") ratePerSecond: Int
    ): Map<String, Any> {
        // 비동기로 실행
        CompletableFuture.runAsync {
            uuidProducerService.sendContinuousMessages(durationSeconds, ratePerSecond)
        }
        
        return mapOf(
            "status" to "started",
            "durationSeconds" to durationSeconds,
            "ratePerSecond" to ratePerSecond,
            "message" to "지속적 메시지 발행이 시작되었습니다. 로그를 확인하세요."
        )
    }
    
    /**
     * 프로듀서 상태 확인
     */
    @GetMapping("/status")
    fun getStatus(): Map<String, Any> {
        val stats = uuidProducerService.getStatistics()
        return mapOf(
            "status" to "running",
            "service" to "UUID Producer Service",
            "topics" to (stats["topics"] ?: emptyList<String>()),
            "totalMessagesSent" to (stats["totalMessagesSent"] ?: 0L)
        )
    }
    
    /**
     * 프로듀서 통계 조회
     */
    @GetMapping("/statistics")
    fun getStatistics(): Map<String, Any> {
        return uuidProducerService.getStatistics()
    }
} 