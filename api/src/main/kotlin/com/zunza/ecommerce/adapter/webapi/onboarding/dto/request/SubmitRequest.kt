package com.zunza.ecommerce.adapter.webapi.onboarding.dto.request

import com.zunza.ecommerce.application.onboarding.service.dto.command.SubmitCommand
import jakarta.validation.constraints.NotBlank

data class SubmitRequest(
    @field:NotBlank(message = "대표자 이름은 필수입니다.")
    val representativeName: String,
    @field:NotBlank(message = "연락 이메일은 필수입니다.")
    val contactEmail: String,
    @field:NotBlank(message = "대표자 번호는 필수입니다.")
    val representativePhone: String,
    @field:NotBlank(message = "사업자등록번호는 필수입니다.")
    val businessNumber: String,
    @field:NotBlank(message = "업체명은 필수입니다.")
    val companyName: String,
    @field:NotBlank(message = "브랜드 이름(국문)은 필수입니다.")
    val brandNameKor: String,
    @field:NotBlank(message = "브랜드 이름(영문)은 필수입니다.")
    val brandNameEng: String,
    @field:NotBlank(message = "브랜드 소개는 필수입니다.")
    val brandDescription: String,
    @field:NotBlank(message = "은행은 필수입니다.")
    val bankName: String,
    @field:NotBlank(message = "계좌번호는 필수입니다.")
    val accountNumber: String,
    @field:NotBlank(message = "예금주명은 필수입니다.")
    val accountHolder: String
) {
    fun toCommand(accountId: Long) =
        SubmitCommand(
            accountId = accountId,
            representativeName = this.representativeName,
            contactEmail = this.contactEmail,
            representativePhone = this.representativePhone,
            businessNumber = this.businessNumber,
            companyName = this.companyName,
            brandNameKor = this.brandNameKor,
            brandNameEng = this.brandNameEng,
            brandDescription = this.brandDescription,
            bankName = this.bankName,
            accountNumber = this.accountNumber,
            accountHolder = this.accountHolder
        )
}
