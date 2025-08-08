package team.sipe.antcode97.consumerfetchmin1024.controller

import org.springframework.web.bind.annotation.*
import team.sipe.antcode97.consumerfetchmin1024.service.FetchMin1024ConsumerService

/**
 * fetch.min.bytes = 1024 컨슈머 제어 API
 * 
 * fetch.min.bytes = 1024 컨슈머의 상태와 통계를 확인할 수 있는 REST API를 제공합니다.
 */
@RestController
@RequestMapping("/api/consumer/fetch-min-1024")
class ConsumerController(
    private val fetchMin1024ConsumerService: FetchMin1024ConsumerService
) {
    
    /**
     * 컨슈머 상태 확인
     */
    @GetMapping("/status")
    fun getStatus(): Map<String, String> {
        return mapOf(
            "status" to "running",
            "consumerType" to "Fetch Min 1024 Consumer",
            "groupId" to FetchMin1024ConsumerService.GROUP_ID,
            "topics" to FetchMin1024ConsumerService.TOPIC_NAME,
            "fetchMinBytes" to "1024",
            "description" to "네트워크 효율성과 지연의 균형 - 일반적인 웹 애플리케이션에 적합"
        )
    }
    
    /**
     * 처리 통계 조회
     */
    @GetMapping("/statistics")
    fun getStatistics(): Map<String, Any> {
        return fetchMin1024ConsumerService.getStatistics()
    }
} 