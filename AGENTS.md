# Test Code Generation Rules

## 중요: 테스트 작성 전 반드시 아래 예시 파일을 참조하세요
- domain Layer
  - core/src/test/kotlin/com/zunza/ecommerce/domain/account/AccountTest.kt
- application layer
  - core/src/test/kotlin/com/zunza/ecommerce/application/account/provided/AccountAuthenticatorTest.kt
- adapter layer
  - api/src/test/kotlin/com/zunza/ecommerce/adapter/webapi/customer/CustomerApiTest.kt

## 1. 기본 원칙 (General Principles)
- **Framework**: Kotlin + JUnit5 + Kotest Assertions
- **Mocking**: MockK
  - **금지**: Mockito 사용 금지
- **Structure**: BDD 스타일 (Given - When - Then)
  - 주석으로 `// given`, `// when`, `// then`을 명시하지 않고, 빈 줄로 단락을 구분한다.
- **Naming**:
  - 클래스명: `{대상클래스}Test`
  - 메서드명: **백틱(\`)을 사용한 한글 서술형**
    - 예: 
    ```kotlin
      @Test
      fun `상품 옵션이 없으면 예외가 발생한다`() { }

## 2. 계층별 테스트 전략 (Layer Strategy)

### A. Domain Layer (Core 모듈)
- **대상**: Entity, Value Object(VO)
- **성격**: **Pure Unit Test** (스프링 컨텍스트 로딩 금지)
- **규칙**:
  - 모든 비즈니스 로직과 제약 조건(validation)을 검증한다.

### B. Application Layer (Core 모듈)
- **대상**: `application/provided` 인터페이스의 **구현체 (Service)**
- **성격**: **Unit Test with Mocks**
- **규칙**:
  - 인터페이스 단위로 작성한다.
  - 외부 의존성(DB, Mail 등)은 모두 Mocking 처리하여 순수 비즈니스 흐름만 검증한다.

### C. Web Adapter Layer (API 모듈)
- **대상**: `adapter/webapi` (Controller)
- **성격**: **Integration Test** (통합 테스트)
- **규칙**:
  - 통합 테스트는 mockK를 사용하지 않는다.
  - `@SpringBootTest` + `@AutoConfigureMockMvc` 사용.
  - 실제 HTTP 요청/응답을 검증하기 위해 `MockMvc`를 사용한다.
  - `ObjectMapper`를 사용하여 Request/Response Body를 검증한다.
  - DB에 올바르게 저장, 수정, 삭제 됐는지 검증한다.
  - **Transaction**: `@Transactional`을 붙여 테스트 후 데이터가 롤백되도록 한다.

## 3. Assertions 가이드 (Kotest Matchers)
- 반드시 Kotest Matchers를 사용한다. (JUnit Assertions 사용 금지)

```kotlin
// ✅ Good Cases
result shouldBe expected
result shouldNotBe null
list shouldHaveSize 3
collection shouldContain element
result.shouldBeInstanceOf<Type>()

// Exception Check
shouldThrow<IllegalArgumentException> {
    service.doSomething()
}.message shouldBe "에러 메시지"
```

## 4. 테스트 실행
- 테스트 대상이 속한 경로에 core가 있다면, 모듈 -> :core:test
- 테스트 대상이 속한 경로에 api가 있다면, 모듈 -> :api:test
- ```bash
  export GRADLE_USER_HOME="${GRADLE_USER_HOME:-$PWD/.gradle-home}"
  mkdir -p "$GRADLE_USER_HOME"
- ```text
  ./gradlew {모듈} --tests {대상클래스}
  ```