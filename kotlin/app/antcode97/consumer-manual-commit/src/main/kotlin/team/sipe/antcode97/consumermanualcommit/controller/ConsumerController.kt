package team.sipe.antcode97.consumermanualcommit.controller

import org.springframework.web.bind.annotation.*
import team.sipe.antcode97.consumermanualcommit.service.ManualCommitConsumerService

/**
 * 수동 커밋 컨슈머 제어 API
 * 
 * 수동 커밋 컨슈머의 상태와 통계를 확인할 수 있는 REST API를 제공합니다.
 */
@RestController
@RequestMapping("/api/consumer/manual-commit")
class ConsumerController(
    private val manualCommitConsumerService: ManualCommitConsumerService
) {
    
    /**
     * 컨슈머 상태 확인
     */
    @GetMapping("/status")
    fun getStatus(): Map<String, String> {
        return mapOf(
            "status" to "running",
            "consumerType" to "Manual Commit Consumer",
            "groupId" to ManualCommitConsumerService.GROUP_ID,
            "topics" to ManualCommitConsumerService.TOPIC_NAME,
            "commitStrategy" to "MANUAL_IMMEDIATE"
        )
    }
    
    /**
     * 처리 통계 조회
     */
    @GetMapping("/statistics")
    fun getStatistics(): Map<String, Any> {
        return manualCommitConsumerService.getStatistics()
    }
} 