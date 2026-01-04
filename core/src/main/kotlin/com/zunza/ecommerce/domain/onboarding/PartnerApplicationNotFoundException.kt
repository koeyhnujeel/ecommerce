package com.zunza.ecommerce.domain.onboarding

class PartnerApplicationNotFoundException(
    message: String = "존재하지 않는 신청서입니다."
) : RuntimeException(message)