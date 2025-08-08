package team.sipe.antcode97.consumermaxpoll5000.controller

import org.springframework.web.bind.annotation.*
import team.sipe.antcode97.consumermaxpoll5000.service.MaxPoll5000ConsumerService

/**
 * max.poll.records = 5000 컨슈머 제어 API
 * 
 * max.poll.records = 5000 컨슈머의 상태와 통계를 확인할 수 있는 REST API를 제공합니다.
 */
@RestController
@RequestMapping("/api/consumer/max-poll-5000")
class ConsumerController(
    private val maxPoll5000ConsumerService: MaxPoll5000ConsumerService
) {
    
    /**
     * 컨슈머 상태 확인
     */
    @GetMapping("/status")
    fun getStatus(): Map<String, String> {
        return mapOf(
            "status" to "running",
            "consumerType" to "Max Poll 5000 Consumer",
            "groupId" to MaxPoll5000ConsumerService.GROUP_ID,
            "topics" to MaxPoll5000ConsumerService.TOPIC_NAME,
            "maxPollRecords" to "5000",
            "description" to "대량 처리 - 높은 처리량, 배치 처리"
        )
    }
    
    /**
     * 처리 통계 조회
     */
    @GetMapping("/statistics")
    fun getStatistics(): Map<String, Any> {
        return maxPoll5000ConsumerService.getStatistics()
    }
} 