package team.sipe.antcode97.consumerbasic.controller

import org.springframework.web.bind.annotation.*
import team.sipe.antcode97.consumerbasic.service.BasicConsumerService

/**
 * 기본 컨슈머 제어 API
 * 
 * 기본 컨슈머의 상태와 통계를 확인할 수 있는 REST API를 제공합니다.
 */
@RestController
@RequestMapping("/api/consumer/basic")
class ConsumerController(
    private val basicConsumerService: BasicConsumerService
) {
    
    /**
     * 컨슈머 상태 확인
     */
    @GetMapping("/status")
    fun getStatus(): Map<String, String> {
        return mapOf(
            "status" to "running",
            "consumerType" to "Basic Consumer",
            "groupId" to BasicConsumerService.GROUP_ID,
            "topics" to BasicConsumerService.TOPIC_NAME
        )
    }
    
    /**
     * 처리 통계 조회
     */
    @GetMapping("/statistics")
    fun getStatistics(): Map<String, Any> {
        return basicConsumerService.getStatistics()
    }
} 