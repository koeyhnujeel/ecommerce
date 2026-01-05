package com.zunza.ecommerce.application.onboarding.service

import com.zunza.ecommerce.application.account.provided.AccountManager
import com.zunza.ecommerce.application.brand.provided.BrandRegister
import com.zunza.ecommerce.application.onboarding.provided.SellerApplicationProcessor
import com.zunza.ecommerce.application.onboarding.required.SellerApplicationRepository
import com.zunza.ecommerce.application.onboarding.required.findByIdOrThrow
import com.zunza.ecommerce.application.onboarding.service.dto.command.RejectCommand
import com.zunza.ecommerce.application.seller.provided.SellerRegister
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SellerApplicationProcessingService(
    private val sellerApplicationRepository: SellerApplicationRepository,
    private val accountManager: AccountManager,
    private val sellerRegister: SellerRegister,
    private val brandRegister: BrandRegister,
) : SellerApplicationProcessor {
    override fun startReview(sellerApplicationId: Long) {
        val partnerApplication = sellerApplicationRepository.findByIdOrThrow(sellerApplicationId)

        partnerApplication.startReview()
    }

    override fun approve(sellerApplicationId: Long) {
        val partnerApplication = sellerApplicationRepository.findByIdOrThrow(sellerApplicationId)

        partnerApplication.approve()

        accountManager.grantPartnerRole(partnerApplication.accountId)

        val sellerId = sellerRegister.register(
            partnerApplication.accountId,
            partnerApplication.id,
            partnerApplication.businessInfo,
            partnerApplication.settlementAccount
        )

        brandRegister.registerBrand(sellerId, partnerApplication.brandInfo)
    }

    override fun reject(command: RejectCommand) {
        val partnerApplication = sellerApplicationRepository.findByIdOrThrow(command.sellerApplicationId)

        partnerApplication.reject(command.reason)
    }
}