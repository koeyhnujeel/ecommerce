# Test Code Generation Rules

## 중요: 테스트 작성 전 반드시 아래 예시 파일을 참조하세요
- domain
  - core/src/test/kotlin/com/zunza/ecommerce/domain/account/AccountTest.kt
- application/provided
  - core/src/test/kotlin/com/zunza/ecommerce/application/account/provided/AccountAuthenticatorTest.kt
- adapter/webapi
  - api/src/test/kotlin/com/zunza/ecommerce/adapter/webapi/customer/CustomerApiTest.kt

## 테스트 프레임워크
- Kotlin + JUnit5 + Kotest assertions 사용
- Mocking: MockK 사용 (Mockito 사용 금지)

## 네이밍 규칙
- 클래스명: `{대상클래스}Test`
- 메서드명: 백틱 사용 한글 서술형
  ```kotlin
  @Test
  fun `상품 옵션이 없으면 예외가 발생한다`() { }
  
## 테스트 구조
- given-when-then
- given, when, then 주석은 작성하지 않음

## Assertions
- Kotest Matchers 사용
  ```kotlin
    // ✅ Good
    result shouldBe expected
    list shouldHaveSize 3
    shouldThrow<IllegalArgumentException> {
    }.message shouldBe

    // ❌ Bad
    assertEquals(expected, result)
    assertTrue(list.size == 3)

## 도메인 테스트 시 추가적인 테스트 대상
- Value Object class

## 테스트 실행
- 테스트 대상이 속한 경로에 core가 있다면, 모듈 -> :core:test
- 테스트 대상이 속한 경로에 api가 있다면, 모듈 -> test
- ```text
  ./gradlew {모듈} --tests {대상클래스}
  ```