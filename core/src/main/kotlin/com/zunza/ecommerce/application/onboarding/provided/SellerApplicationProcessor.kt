package com.zunza.ecommerce.application.onboarding.provided

import com.zunza.ecommerce.application.onboarding.service.dto.command.RejectCommand

interface SellerApplicationProcessor {
    fun approve(sellerApplicationId: Long)

    fun startReview(sellerApplicationId: Long)

    fun reject(command: RejectCommand)
}