package team.sipe.antcode97.consumermaxpoll100.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties

/**
 * max.poll.records = 100 카프카 컨슈머 설정
 * 
 * 실무에서 중요한 max.poll.records 설정을 테스트합니다.
 * - max.poll.records = 100 (소량 처리)
 * - 빠른 응답이 필요한 경우
 * - 메모리 사용량을 최소화하고 싶은 경우
 */
@Configuration
class KafkaConsumerConfig {
    
    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String
    
    /**
     * max.poll.records = 100 컨슈머 팩토리 설정
     * 
     * 이 설정은 소량 처리를 위해 사용됩니다.
     * - 한 번에 최대 100개 레코드만 가져옴
     * - 빠른 응답이 필요한 경우
     * - 메모리 사용량을 최소화하고 싶은 경우
     */
    @Bean
    fun maxPoll100ConsumerFactory(): ConsumerFactory<String, String> {
        val configProps = mapOf(
            // 기본 설정
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            
            // 그룹 ID 설정
            ConsumerConfig.GROUP_ID_CONFIG to "max-poll-100-consumer-group",
            
            // 오프셋 리셋 정책
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
            
            // 수동 커밋 설정
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false,
            ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG to 0,
            
            // 세션 타임아웃 설정
            ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG to 30000,
            ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG to 3000,
            
            // 핵심 설정: max.poll.records = 100 (소량 처리)
            ConsumerConfig.MAX_POLL_RECORDS_CONFIG to 100,           // 한 번에 최대 100개 레코드
            ConsumerConfig.FETCH_MIN_BYTES_CONFIG to 1024,           // 최소 1KB
            ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG to 500,          // 최대 500ms 대기
            
            // 연결 설정
            ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG to 50,
            ConsumerConfig.RETRY_BACKOFF_MS_CONFIG to 100,
            
            // 고급 설정
            ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG to 300000,    // 5분
            ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG to 30000,
            
            // 파티션 할당 전략
            ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG to 
                "org.apache.kafka.clients.consumer.RangeAssignor"
        )
        
        return DefaultKafkaConsumerFactory(configProps)
    }
    
    /**
     * max.poll.records = 100 카프카 리스너 컨테이너 팩토리
     */
    @Bean
    fun maxPoll100KafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = maxPoll100ConsumerFactory()
        
        // 컨테이너 설정
        factory.containerProperties.apply {
            // 수동 커밋 설정
            ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE
            
            // 에러 핸들러 설정
        }
        
        // 동시성 설정
        factory.setConcurrency(1)
        
        return factory
    }
} 