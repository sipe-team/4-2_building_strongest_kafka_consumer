rootProject.name = "strongest-kafka-consumer"

// 프로듀서 모듈
include("producer")

// 핵심 성능 테스트 컨슈머들
include("consumer-basic")           // 기본 컨슈머 (자동 커밋) - 비교 기준
include("consumer-manual-commit")   // 수동 커밋 컨슈머 - 안전성 vs 성능

// fetch.min.bytes 설정 비교 (네트워크 효율성)
include("consumer-fetch-min-1024")  // fetch.min.bytes = 1024 (1KB, 균형)

// max.poll.records 설정 비교 (처리량)
include("consumer-max-poll-100")    // max.poll.records = 100 (소량, 실시간)
include("consumer-max-poll-5000")   // max.poll.records = 5000 (대량, 최고 성능)

// Ultra Optimized 컨슈머 (극대 성능)
include("consumer-ultra-optimized") // Ultra Optimized 컨슈머 - 극대 성능
