package team.sipe.antcode97.consumerultraoptimized.config

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
 * Ultra Optimized 카프카 컨슈머 설정
 * 
 * 모든 최적화 설정을 조합한 최고 성능 컨슈머입니다.
 * - max.poll.records = 10000 (극대량 처리)
 * - fetch.min.bytes = 4096 (4KB, 네트워크 효율성 극대화)
 * - fetch.max.wait.ms = 50 (빠른 응답)
 * - session.timeout.ms = 60000 (안정적인 세션)
 * - heartbeat.interval.ms = 10000 (효율적인 하트비트)
 * - enable.auto.commit = false (수동 커밋)
 * - ack.mode = MANUAL_IMMEDIATE (즉시 커밋)
 */
@Configuration
class KafkaConsumerConfig {
    
    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String
    
    /**
     * Ultra Optimized 컨슈머 팩토리 설정
     * 
     * 모든 최적화 설정을 조합한 최고 성능 설정입니다.
     * - 한 번에 최대 10000개 레코드를 가져옴
     * - 네트워크 효율성 극대화
     * - 빠른 응답과 높은 처리량의 균형
     */
    @Bean
    fun ultraOptimizedConsumerFactory(): ConsumerFactory<String, String> {
        val configProps = mapOf(
            // 기본 설정
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            
            // 그룹 ID 설정
            ConsumerConfig.GROUP_ID_CONFIG to "ultra-optimized-consumer-group",
            
            // 오프셋 리셋 정책
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
            
            // 수동 커밋 설정
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false,
            ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG to 0,
            
            // 세션 타임아웃 설정 (안정성 향상)
            ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG to 60000,        // 60초
            ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG to 10000,     // 10초
            
            // 핵심 설정: 극대량 처리
            ConsumerConfig.MAX_POLL_RECORDS_CONFIG to 10000,         // 한 번에 최대 10000개 레코드
            ConsumerConfig.FETCH_MIN_BYTES_CONFIG to 4096,           // 최소 4KB (네트워크 효율성 극대화)
            ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG to 50,           // 최대 50ms 대기 (빠른 응답)
            
            // 연결 설정 (빠른 재연결)
            ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG to 25,
            ConsumerConfig.RETRY_BACKOFF_MS_CONFIG to 50,
            
            // 고급 설정 (여유로운 폴링)
            ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG to 600000,    // 10분
            ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG to 60000,       // 60초
            
            // 파티션 할당 전략 (효율적인 할당)
            ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG to 
                "org.apache.kafka.clients.consumer.StickyAssignor"
        )
        
        return DefaultKafkaConsumerFactory(configProps)
    }
    
    /**
     * Ultra Optimized 카프카 리스너 컨테이너 팩토리
     */
    @Bean
    fun ultraOptimizedKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = ultraOptimizedConsumerFactory()
        
        // 컨테이너 설정
        factory.containerProperties.apply {
            // 수동 커밋 설정 (즉시 커밋)
            ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE
            
            // 에러 핸들러 설정
        }
        
        // 동시성 설정 (단일 스레드로 처리량 극대화)
        factory.setConcurrency(1)
        
        return factory
    }
} 