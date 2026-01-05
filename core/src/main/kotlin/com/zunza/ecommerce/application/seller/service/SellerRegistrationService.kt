package com.zunza.ecommerce.application.seller.service

import com.zunza.ecommerce.application.seller.provided.SellerRegister
import com.zunza.ecommerce.application.seller.required.SellerRepository
import com.zunza.ecommerce.domain.seller.BusinessInfo
import com.zunza.ecommerce.domain.seller.Seller
import com.zunza.ecommerce.domain.shared.BankAccount
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SellerRegistrationService(
    private val sellerRepository: SellerRepository,
) : SellerRegister {
    override fun register(
        accountId: Long,
        sellerApplicationId: Long,
        businessInfo: BusinessInfo,
        settlementAccount: BankAccount
    ): Long {
        val seller = Seller.register(
            accountId = accountId,
            sellerApplicationId = sellerApplicationId,
            businessInfo = businessInfo,
            settlementAccount = settlementAccount
        )

        return sellerRepository.save(seller).id
    }
}