package com.zunza.ecommerce.application.onboarding.service.dto.command

data class SubmitCommand(
    val accountId: Long,
    val representativeName: String,
    val contractEmail: String,
    val representativePhone: String,
    val businessNumber: String,
    val companyName: String,
    val brandNameKor: String,
    val brandNameEng: String,
    val brandDescription: String,
    val bankName: String,
    val accountNumber: String,
    val accountHolder: String
)
