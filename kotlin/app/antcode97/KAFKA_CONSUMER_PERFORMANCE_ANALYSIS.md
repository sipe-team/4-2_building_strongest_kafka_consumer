# Kafka Consumer 성능 분석 보고서

## 📊 성능 측정 결과 요약

| 모듈 | 처리 속도 (msg/sec) | 평균 처리 시간 (ms/msg) | 성능 순위 |
|------|-------------------|----------------------|----------|
| **consumer-basic** | **48,968.76** | **0.02** | 🥇 **1위** |
| consumer-fetch-min-1024 | 8,368.83 | 0.12 | 🥈 2위 |
| consumer-manual-commit | 7,322.98 | 0.14 | 🥉 3위 |
| consumer-max-poll-5000 | 7,062.85 | 0.14 | 4위 |
| consumer-ultra-optimized | 6,339.56 | 0.16 | 5위 |


---

## 📋 모듈별 상세 분석

### 1. consumer-basic (🥇 1위)

#### 🔧 설정
```yaml
# 기본 설정
enable-auto-commit: true
auto-commit-interval: 5000ms
max-poll-records: 500
fetch-min-size: 1
fetch-max-wait: 500ms
```

#### 📈 성능
- **처리 속도**: 48,968.76 msg/sec
- **평균 처리 시간**: 0.02 ms/msg
- **특징**: 가장 단순한 설정으로 최고 성능

#### 💡 성공 요인
1. **자동 커밋**: 커밋 오버헤드 최소화
2. **적절한 배치 크기**: 500개로 균형잡힌 처리
3. **단순한 설정**: 복잡한 설정으로 인한 오버헤드 없음
4. **빠른 응답**: fetch.max.wait = 500ms로 적절한 대기 시간

---

### 2. consumer-fetch-min-1024 (🥈 2위)

#### 🔧 설정
```yaml
# fetch.min.bytes 최적화
enable-auto-commit: false
fetch-min-size: 1024        # 1KB
fetch-max-wait: 500ms
max-poll-records: 500
```

#### 📈 성능
- **처리 속도**: 8,368.83 msg/sec
- **평균 처리 시간**: 0.12 ms/msg

#### 💡 분석
- **네트워크 효율성**: 1KB 단위로 데이터 수집
- **수동 커밋 오버헤드**: 자동 커밋 대비 성능 저하
- **적절한 대기 시간**: 500ms로 균형잡힌 설정

---

### 3. consumer-manual-commit (🥉 3위)

#### 🔧 설정
```yaml
# 수동 커밋 설정
enable-auto-commit: false
auto-commit-interval: 0ms
max-poll-records: 500
fetch-min-size: 1
fetch-max-wait: 500ms
```

#### 📈 성능
- **처리 속도**: 7,322.98 msg/sec
- **평균 처리 시간**: 0.14 ms/msg

#### 💡 분석
- **수동 커밋 오버헤드**: 매 메시지마다 커밋 처리
- **안정성 vs 성능**: 신뢰성은 높지만 성능은 저하
- **적용 시나리오**: 정확히 한 번 전달이 중요한 경우

---

### 4. consumer-max-poll-5000 (4위)

#### 🔧 설정
```yaml
# 대용량 배치 처리
enable-auto-commit: false
max-poll-records: 5000      # 5000개 레코드
fetch-min-size: 1024        # 1KB
fetch-max-wait: 500ms
```

#### 📈 성능
- **처리 속도**: 7,062.85 msg/sec
- **평균 처리 시간**: 0.14 ms/msg

#### 💡 분석
- **메모리 압박**: 대용량 배치로 인한 GC 압박
- **처리 지연**: 배치가 완성될 때까지 대기
- **적용 시나리오**: 대용량 데이터 처리 시

---

### 5. consumer-ultra-optimized (5위)

#### 🔧 설정
```yaml
# 극대화된 최적화 설정
enable-auto-commit: false
max-poll-records: 10000     # 10000개 레코드
fetch-min-size: 4096        # 4KB
fetch-max-wait: 50ms        # 빠른 응답
session-timeout: 60000ms    # 60초
heartbeat-interval: 10000ms # 10초
partition-assignment-strategy: StickyAssignor
```

#### 📈 성능
- **처리 속도**: 6,339.56 msg/sec
- **평균 처리 시간**: 0.16 ms/msg

#### 💡 분석
- **과도한 최적화**: 복잡한 설정으로 인한 오버헤드
- **메모리 압박**: 10000개 레코드로 인한 GC 압박
- **네트워크 효율성**: 4KB 단위로 효율적이지만 오버헤드 증가

