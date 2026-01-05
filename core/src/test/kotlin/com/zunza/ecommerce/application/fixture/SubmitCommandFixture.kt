package com.zunza.ecommerce.application.fixture

import com.zunza.ecommerce.application.onboarding.service.dto.command.SubmitCommand

object SubmitCommandFixture {

    fun createSubmitCommand(
        accountId: Long = 1L,
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
