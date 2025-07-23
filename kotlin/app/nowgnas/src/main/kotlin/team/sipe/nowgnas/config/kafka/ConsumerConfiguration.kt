package team.sipe.nowgnas.config.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties


@EnableKafka
@Configuration
class AckModeManualKafkaConsumerConfiguration(
    @Value("\${spring.kafka.bootstrap-servers}")
    private val bootstrapServers: String,
    @Value("\${spring.kafka.consumer.group-id}")
    private val groupId: String,
) {
    companion object {
        private const val CONCURRENCY = 1
        private const val LATEST = "latest"
    }

    @Primary
    @Bean("ackModeManualKafkaConsumerFactory")
    fun ackModeManualConsumerFactory(): ConsumerFactory<String, String> {
        return DefaultKafkaConsumerFactory(ackModeManualConsumerConfig())
    }

    @Bean("ackModeManualKafkaContainerFactory")
    fun ackModeManualKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        return ConcurrentKafkaListenerContainerFactory<String, String>().apply {
            consumerFactory = ackModeManualConsumerFactory()
            containerProperties.ackMode = ContainerProperties.AckMode.MANUAL
            setConcurrency(CONCURRENCY)
        }
    }

    @Bean
    fun ackModeManualConsumerConfig(): Map<String, Any> {
        return mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            // 한번에 가져올 수 있는 최소 데이터 크기
            ConsumerConfig.FETCH_MIN_BYTES_CONFIG to 100,
            // 한 번의 가져오기 요청으로 가져올 수 있는 최대 데이터 크기
            ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG to 1000,
            // 컨슈머 그룹 id
            ConsumerConfig.GROUP_ID_CONFIG to groupId,
            // 초기 오프셋이 없거나 현재 오프셋이 존재하지 않을 경우 초기화 전략
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to LATEST,
            // 백그라운드 오프셋 커밋 여부
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false,
            // 하트비트 주기 session.timeout.ms보다 작은 값으로 설정 보통 session.timeout.ms의 1/3
            ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG to 3000,
            // session timeout 전까지 하트비트를 보내지 않으면 컨슈머가 종료된 것으로 인지
            ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG to 10000,
            // 트랜잭션 컨슈머에서 사용되는 옵션 read_committed는 트랜잭션이 완료된 메시지를 읽음 (기본값: read_uncommitted)
            ConsumerConfig.ISOLATION_LEVEL_CONFIG to "read_committed",
            // 한 번의 요청으로 가져오는 최대 메시지 개수
            ConsumerConfig.MAX_POLL_RECORDS_CONFIG to 100,
            // 파티션 할당 전략 (기본 값: Range 파티션이 2개 이상이라면 Cooperative 파티셔너 사용 가능)
            ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG to "org.apache.kafka.clients.consumer.RangeAssignor",
            // fetch.min.bytes에 의해 설정된 데이터보다 적은 경우 요청에 대한 응답을 기다리는 최대 시간
            ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG to 500000,
            // 요청 응답에 대한 클라이언트의 최대 대기 시간, 타임아웃 시간 동안 응답을 받지 못하면 요청을 다시 보낸다
            // 불필요한 재시도를 줄이려면 replica.lag.time.max.ms 보다 큰 값이어야 한다
            // replica.lag.time.max.ms의 기본값은 30000ms 이다
            ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG to 500000,
        )
    }
}
