package team.sipe.antcode97.consumermaxpoll100.controller

import org.springframework.web.bind.annotation.*
import team.sipe.antcode97.consumermaxpoll100.service.MaxPoll100ConsumerService

/**
 * max.poll.records = 100 컨슈머 제어 API
 * 
 * max.poll.records = 100 컨슈머의 상태와 통계를 확인할 수 있는 REST API를 제공합니다.
 */
@RestController
@RequestMapping("/api/consumer/max-poll-100")
class ConsumerController(
    private val maxPoll100ConsumerService: MaxPoll100ConsumerService
) {
    
    /**
     * 컨슈머 상태 확인
     */
    @GetMapping("/status")
    fun getStatus(): Map<String, String> {
        return mapOf(
            "status" to "running",
            "consumerType" to "Max Poll 100 Consumer",
            "groupId" to MaxPoll100ConsumerService.GROUP_ID,
            "topics" to MaxPoll100ConsumerService.TOPIC_NAME,
            "maxPollRecords" to "100",
            "description" to "소량 처리 - 빠른 응답, 메모리 최소화"
        )
    }
    
    /**
     * 처리 통계 조회
     */
    @GetMapping("/statistics")
    fun getStatistics(): Map<String, Any> {
        return maxPoll100ConsumerService.getStatistics()
    }
} 