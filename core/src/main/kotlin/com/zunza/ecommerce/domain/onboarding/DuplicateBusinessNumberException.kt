package com.zunza.ecommerce.domain.onboarding

class DuplicateBusinessNumberException(
    message: String = "이미 신청된 사업자등록번호입니다."
) : RuntimeException(message)