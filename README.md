# 최강 kafka consumer 만들기

## 미션 진행 일정

- 매주 화요일 22:00
- 방식: 온라인

## 미션 목적

- kafka 동작의 전반적인 이해
- 강력한 consumer 만들기

## 미션 진행 방식

- 각자 맡은 챕터를 정리해서 공유합니다.
- 기본 코드를 보고 메시지 발행, 소비에 대해 이해합니다.
- 책을 읽으며 실습을 진행합니다.
- 데이터를 안정적으로 처리할 수 있는 최강 consumer를 만들어봅니다.

## 카프카 핵심 가이드

[책 구매 링크](https://product.kyobobook.co.kr/detail/S000201464167)  
[원서](<https://github.com/melkhazen/Kafka-The-Definitive-Guide-2nd-Edition-pdf/blob/main/Kafka%20The%20Definitive%20Guide%20Real-Time%20Data%20and%20Stream%20Processing%20at%20Scale,%20Second%20Edition%20by%20Gwen%20Shapira,%20Todd%20Palino,%20Rajini%20Sivaram,%20Krit%20Petty%20(z-lib.org).pdf>)

## 미션 진행

|    날짜    | 내용                                                                                                      |      공유자       |
| :--------: | :-------------------------------------------------------------------------------------------------------- | :---------------: |
| 2025.07.08 | kafka 킥오프                                                                                              |      이상원       |
| 2025.07.15 | CHAPTER 3 카프카 프로듀서: 카프카에 메시지 쓰기<br> CHAPTER 4 카프카 컨슈머: 카프카에서 데이터 읽기       | 유지예 <br>장세은 |
| 2025.07.22 | CHAPTER 6 카프카 내부 메커니즘<br>CHAPTER 7 신뢰성 있는 데이터 전달<br>CHAPTER 8 ‘정확히 한 번’ 의미 구조 |      이상원       |
| 2025.07.29 |                                                                                                           |                   |
| 2025.08.05 |                                                                                                           |                   |

### 참고자료

- https://github.com/wilump-labs/spring-kafka-in-actions

## 스크립트 사용

### kafka 브로커, kafka 대시보드, mysql 실행

```bash
sh start.sh
```

- Kafka-UI: 8081

  - URL: http://localhost:8081

- CMAK (Kafka Manager): 9000

  - URL: http://localhost:9000

- Redpanda Console: 8989

  - URL: http://localhost:8989

- message-process-topic 이름의 토픽 미리 생성 되어 있음
  - 파티션 3개

### kafka 중지

```bash
sh stop.sh
```

### 메시지 발행

```bash
sh produce.sh
```

- 각 폴더 안에 있는 produce.sh 실행
