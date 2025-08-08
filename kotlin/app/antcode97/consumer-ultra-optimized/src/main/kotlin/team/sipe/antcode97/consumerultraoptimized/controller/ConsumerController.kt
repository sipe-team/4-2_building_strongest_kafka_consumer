package team.sipe.antcode97.consumerultraoptimized.controller

import org.springframework.web.bind.annotation.*
import team.sipe.antcode97.consumerultraoptimized.service.UltraOptimizedConsumerService

/**
 * Ultra Optimized 컨슈머 제어 API
 * 
 * Ultra Optimized 컨슈머의 상태와 통계를 확인할 수 있는 REST API를 제공합니다.
 */
@RestController
@RequestMapping("/api/consumer/ultra-optimized")
class ConsumerController(
    private val ultraOptimizedConsumerService: UltraOptimizedConsumerService
) {
    
    /**
     * 컨슈머 상태 확인
     */
    @GetMapping("/status")
    fun getStatus(): Map<String, String> {
        return mapOf(
            "status" to "running",
            "consumerType" to "Ultra Optimized Consumer",
            "groupId" to UltraOptimizedConsumerService.GROUP_ID,
            "topics" to UltraOptimizedConsumerService.TOPIC_NAME,
            "maxPollRecords" to "10000",
            "fetchMinBytes" to "4096",
            "fetchMaxWaitMs" to "50",
            "sessionTimeoutMs" to "60000",
            "heartbeatIntervalMs" to "10000",
            "description" to "극대량 처리 + 네트워크 효율성 극대화 + 빠른 응답"
        )
    }
    
    /**
     * 처리 통계 조회
     */
    @GetMapping("/statistics")
    fun getStatistics(): Map<String, Any> {
        return ultraOptimizedConsumerService.getStatistics()
    }
} 