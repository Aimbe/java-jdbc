# 만들면서 배우는 스프링
[Next Step - 과정 소개](https://edu.nextstep.camp/c/4YUvqn9V)

## JDBC 라이브러리 구현하기

### 학습목표
- JDBC 라이브러리를 구현하는 경험을 함으로써 중복을 제거하는 연습을 한다.
- Transaction 적용을 위해 알아야할 개념을 이해한다.

### 시작 가이드
1. 이전 미션에서 진행한 코드를 사용하고 싶다면, 마이그레이션 작업을 진행합니다.
    - 학습 테스트는 강의 시간에 풀어봅시다.
2. LMS의 1단계 미션부터 진행합니다.

## 준비 사항
- 강의 시작 전에 docker를 설치해주세요.

## 학습 테스트
1. [ConnectionPool](study/src/test/java/connectionpool)
2. [Transaction](study/src/test/java/transaction)

## 요구사항 정리
### 🚀 4단계 - 트랜잭션 동기화 구현하기

-[ ] Transaction synchronization 적용하기
  - [ ] 트랜잭션을 적용할 수 있는 기능을 Connection 객체를 파라미터로 전달받아 사용하지 않도록 개선한다.
  - [ ] 스프링 PlatformTransactionManager 를  로컬 트랜잭션, 글로벌 트랜잭션, JTA 라는 세 가지 키워드와 학습하자 
-[ ] 트랜잭션 서비스 추상화하기
  - [ ] 트랜잭션 서비스를 추상화하여 비즈니스 로직과 데이터 액세스 로직을 분리해보자.
  - [ ] testTransactionRollback를 변경 후 , UserServiceTest 을 성공시키자

```
@Test
void testTransactionRollback() {
    // 트랜잭션 롤백 테스트를 위해 mock으로 교체
    final var userHistoryDao = new MockUserHistoryDao(jdbcTemplate);
    // 애플리케이션 서비스
    final var appUserService = new AppUserService(userDao, userHistoryDao);
    // 트랜잭션 서비스 추상화
    final var userService = new TxUserService(appUserService);

    final var newPassword = "newPassword";
    final var createdBy = "gugu";
    // 트랜잭션이 정상 동작하는지 확인하기 위해 의도적으로 MockUserHistoryDao에서 예외를 발생시킨다.
    assertThrows(DataAccessException.class,
            () -> userService.changePassword(1L, newPassword, createdBy));

    final var actual = userService.findById(1L);

    assertThat(actual.getPassword()).isNotEqualTo(newPassword);
}
```
