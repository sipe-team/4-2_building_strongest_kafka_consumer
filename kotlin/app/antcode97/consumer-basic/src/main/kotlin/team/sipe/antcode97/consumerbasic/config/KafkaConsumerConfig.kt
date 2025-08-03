package team.sipe.antcode97.consumerbasic.config

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
 * 기본 카프카 컨슈머 설정
 * 
 * 카프카 핵심 가이드 p.120-140의 기본 컨슈머 설정을 구현합니다.
 * - 자동 커밋
 * - 기본 파티션 할당 전략
 * - 기본 오프셋 리셋 정책
 */
@Configuration
class KafkaConsumerConfig {
    
    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String
    
    /**
     * 기본 컨슈머 팩토리 설정
     * 
     * 카프카 핵심 가이드 p.125의 기본 설정을 사용합니다.
     */
    @Bean
    fun basicConsumerFactory(): ConsumerFactory<String, String> {
        val configProps = mapOf(
            // 기본 설정
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            
            // 그룹 ID 설정
            ConsumerConfig.GROUP_ID_CONFIG to "basic-consumer-group",
            
            // 오프셋 리셋 정책 - 카프카 핵심 가이드 p.127
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
            
            // 자동 커밋 설정 - 카프카 핵심 가이드 p.128
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to true,
            ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG to 5000,
            
            // 세션 타임아웃 설정
            ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG to 30000,
            ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG to 3000,
            
            // 메시지 가져오기 설정
            ConsumerConfig.FETCH_MIN_BYTES_CONFIG to 1,
            ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG to 500,
            ConsumerConfig.MAX_POLL_RECORDS_CONFIG to 500,
            
            // 연결 설정
            ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG to 50,
            ConsumerConfig.RETRY_BACKOFF_MS_CONFIG to 100,
            
            // 고급 설정
            ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG to 300000,  // 5분
            ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG to 30000,
            
            // 파티션 할당 전략 - 카프카 핵심 가이드 p.135
            ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG to 
                "org.apache.kafka.clients.consumer.RangeAssignor"
        )
        
        return DefaultKafkaConsumerFactory(configProps)
    }
    
    /**
     * 기본 카프카 리스너 컨테이너 팩토리
     * 
     * 카프카 핵심 가이드 p.140의 컨테이너 설정을 사용합니다.
     */
    @Bean
    fun basicKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = basicConsumerFactory()
        
        // 컨테이너 설정
        factory.containerProperties.apply {
            // 수동 커밋 비활성화 (자동 커밋 사용)
            ackMode = ContainerProperties.AckMode.BATCH
            
            // 에러 핸들러 설정 (Spring Kafka 2.8+ 버전에서는 다른 방식 사용)
            // }
        }
        
        // 동시성 설정
        factory.setConcurrency(1)  // 기본은 단일 스레드
        
        return factory
    }
} 