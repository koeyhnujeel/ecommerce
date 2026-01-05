package com.zunza.ecommerce.adapter.webapi.onboarding

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.zunza.ecommerce.adapter.ApiResponse
import com.zunza.ecommerce.adapter.persistence.brand.BrandJpaRepository
import com.zunza.ecommerce.adapter.persistence.seller.SellerJpaRepository
import com.zunza.ecommerce.adapter.webapi.onboarding.dto.request.RejectRequest
import com.zunza.ecommerce.adapter.webapi.onboarding.dto.response.SubmitResponse
import com.zunza.ecommerce.adapter.webapi.onboarding.fixture.SubmitRequestFixture
import com.zunza.ecommerce.application.account.provided.AccountAuthenticator
import com.zunza.ecommerce.application.account.provided.AccountManager
import com.zunza.ecommerce.application.account.provided.AccountRegister
import com.zunza.ecommerce.application.account.required.AccountRepository
import com.zunza.ecommerce.application.account.required.TokenProvider
import com.zunza.ecommerce.application.account.required.findByIdOrThrow
import com.zunza.ecommerce.application.account.service.dto.command.AccountRegisterCommand
import com.zunza.ecommerce.application.account.service.dto.command.LoginCommand
import com.zunza.ecommerce.application.onboarding.provided.SellerApplicationProcessor
import com.zunza.ecommerce.application.onboarding.provided.SellerApplicationRegister
import com.zunza.ecommerce.application.onboarding.required.SellerApplicationRepository
import com.zunza.ecommerce.application.onboarding.required.findByIdOrThrow
import com.zunza.ecommerce.application.onboarding.service.dto.command.SubmitCommand
import com.zunza.ecommerce.config.TestConfiguration
import com.zunza.ecommerce.config.TestContainersConfiguration
import com.zunza.ecommerce.domain.account.UserRole
import com.zunza.ecommerce.domain.onboarding.ApplicationStatus
import com.zunza.ecommerce.domain.onboarding.ReviewResult
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(classes = [TestConfiguration::class, TestContainersConfiguration::class])
class OnboardingApiTest(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper,
    val tokenProvider: TokenProvider,

    val accountRepository: AccountRepository,
    val brandJpaRepository: BrandJpaRepository,
    val sellerJpaRepository: SellerJpaRepository,
    val sellerApplicationRepository: SellerApplicationRepository,

    val accountAuthenticator: AccountAuthenticator,
    val sellerApplicationRegister: SellerApplicationRegister,
    val sellerApplicationProcessor: SellerApplicationProcessor,
    val accountRegister: AccountRegister,
    val accountManager: AccountManager
) {
    private var accountId = 0L
    lateinit var accessToken: String
    lateinit var refreshToken: String

    @BeforeEach
    fun setUp() {
        val registerCommand = AccountRegisterCommand(
            email = "zunza@email.com",
            password = "password1!",
            name = "홍길동",
            phone = "01012345678",
        )

        accountId = accountRegister.registerCustomerAccount(registerCommand)

        accountManager.activateCustomerAccount(accountId)

        val loginCommand = LoginCommand(registerCommand.email, registerCommand.password)

        val loginResult = accountAuthenticator.login(loginCommand)

        accessToken = loginResult.accessToken
        refreshToken = loginResult.refreshToken
    }

    @Test
    fun submit() {
        val request = SubmitRequestFixture.createSubmitRequest()

        val result = mockMvc.post("/api/partnerApplications") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            cookie(
                Cookie("accessToken", accessToken),
                Cookie("refreshToken", refreshToken),
                )
        }.andExpect {
            status { isCreated() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.data.partnerApplicationId") { exists() }
            jsonPath("$.timestamp") { exists() }
        }.andReturn()

        val response: ApiResponse<SubmitResponse> = objectMapper.readValue(result.response.contentAsString)

        val partnerApplication = sellerApplicationRepository.findByIdOrThrow(response.data!!.partnerApplicationId)

        partnerApplication.applicantInfo.representativeName shouldBe request.representativeName
        partnerApplication.applicantInfo.contactEmail.address shouldBe request.contactEmail
        partnerApplication.applicantInfo.representativePhone shouldBe request.representativePhone
        partnerApplication.businessInfo.businessNumber shouldBe request.businessNumber
        partnerApplication.businessInfo.companyName shouldBe request.companyName
        partnerApplication.brandInfo.nameKor shouldBe request.brandNameKor
        partnerApplication.brandInfo.nameEng shouldBe request.brandNameEng
        partnerApplication.brandInfo.description shouldBe request.brandDescription
        partnerApplication.settlementAccount.bankName shouldBe request.bankName
        partnerApplication.settlementAccount.accountNumber shouldBe request.accountNumber
        partnerApplication.settlementAccount.accountHolder shouldBe request.accountHolder
        partnerApplication.status shouldBe ApplicationStatus.SUBMITTED
        partnerApplication.submittedAt shouldNotBe null
        partnerApplication.reviewHistories shouldHaveSize 0
    }

    @Test
    fun startReview() {
        val result = sellerApplicationRegister.submit(createSubmitCommand())
        val adminAccessToken = tokenProvider.generateAccessToken(100L, listOf(UserRole.ROLE_ADMIN))

        mockMvc.patch("/api/partnerApplications/${result.partnerApplicationId}/review") {
            contentType = MediaType.APPLICATION_JSON
            cookie(Cookie("accessToken", adminAccessToken))
        }.andExpect {
            status { isNoContent() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.timestamp") { exists() }
        }

        val partnerApplication = sellerApplicationRepository.findByIdOrThrow(result.partnerApplicationId)

        partnerApplication.status shouldBe ApplicationStatus.REVIEWING
    }

    @Test
    fun approve() {
        val result = sellerApplicationRegister.submit(createSubmitCommand())

        sellerApplicationProcessor.startReview(result.partnerApplicationId)

        val adminAccessToken = tokenProvider.generateAccessToken(100L, listOf(UserRole.ROLE_ADMIN))

        mockMvc.post("/api/partnerApplications/${result.partnerApplicationId}/approval") {
            contentType = MediaType.APPLICATION_JSON
            cookie(Cookie("accessToken", adminAccessToken))
        }.andExpect {
            status { isNoContent() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.timestamp") { exists() }
        }

        val partnerApplication = sellerApplicationRepository.findByIdOrThrow(result.partnerApplicationId)

        partnerApplication.status shouldBe ApplicationStatus.APPROVED
        partnerApplication.reviewHistories shouldHaveSize 1
        partnerApplication.reviewHistories[0].result shouldBe ReviewResult.APPROVED
        partnerApplication.reviewHistories[0].comment shouldBe "입점이 승인됐습니다."

        val account = accountRepository.findByIdOrThrow(accountId)

        account.roles shouldHaveSize 2
        account.roles shouldContain UserRole.ROLE_CUSTOMER
        account.roles shouldContain UserRole.ROLE_PARTNER

        val partner = sellerJpaRepository.findAll()[0]

        partner.accountId shouldBe account.id
        partner.sellerApplicationId shouldBe result.partnerApplicationId
        partner.businessInfo shouldBe partnerApplication.businessInfo
        partner.settlementAccount shouldBe partnerApplication.settlementAccount

        val brand = brandJpaRepository.findAll()[0]

        brand.sellerId shouldBe partner.id
        brand.brandInfo shouldBe partnerApplication.brandInfo
    }

    @Test
    fun reject() {
        val result = sellerApplicationRegister.submit(createSubmitCommand())

        sellerApplicationProcessor.startReview(result.partnerApplicationId)

        val request = RejectRequest("조건 불충분")
        val adminAccessToken = tokenProvider.generateAccessToken(100L, listOf(UserRole.ROLE_ADMIN))

        mockMvc.post("/api/partnerApplications/${result.partnerApplicationId}/rejection") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            cookie(Cookie("accessToken", adminAccessToken))
        }.andExpect {
            status { isNoContent() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.timestamp") { exists() }
        }

        val partnerApplication = sellerApplicationRepository.findByIdOrThrow(result.partnerApplicationId)

        partnerApplication.status shouldBe ApplicationStatus.REJECTED
        partnerApplication.reviewHistories shouldHaveSize 1
        partnerApplication.reviewHistories[0].result shouldBe ReviewResult.REJECTED
        partnerApplication.reviewHistories[0].comment shouldBe request.reason
    }

    @Test
    fun getSellerApplicationStatus() {
        val result = sellerApplicationRegister.submit(createSubmitCommand())

        mockMvc.get("/api/partnerApplications/me") {
            contentType = MediaType.APPLICATION_JSON
            cookie(Cookie("accessToken", accessToken),
                Cookie("refreshToken", refreshToken)
                )
        }.andExpect {
            status { isOk() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.data.sellerApplicationId") { value(result.partnerApplicationId) }
            jsonPath("$.data.representativeName") { value("홍길동") }
            jsonPath("$.data.status") { value("SUBMITTED") }
            jsonPath("$.data.submittedAt") { exists() }
            jsonPath("$.timestamp") { exists() }
        }
    }

    private fun createSubmitCommand(
        representativeName: String = "홍길동",
        contactEmail: String = "gildong@email.com",
        representativePhone: String = "01012345678",
        businessNumber: String = "1234567890",
        companyName: String = "홍길동 컴퍼니",
        brandNameKor: String = "홍길동 브랜드",
        brandNameEng: String = "Hong Gildong Brand",
        brandDescription: String = "홍길동의 브랜드를 소개합니다.",
        bankName: String = "국민은행",
        accountNumber: String = "12345678901234",
        accountHolder: String = "홍길동"
    ) = SubmitCommand(
        accountId = accountId,
        representativeName = representativeName,
        contactEmail = contactEmail,
        representativePhone = representativePhone,
        businessNumber = businessNumber,
        companyName = companyName,
        brandNameKor = brandNameKor,
        brandNameEng = brandNameEng,
        brandDescription = brandDescription,
        bankName = bankName,
        accountNumber = accountNumber,
        accountHolder = accountHolder
    )
}