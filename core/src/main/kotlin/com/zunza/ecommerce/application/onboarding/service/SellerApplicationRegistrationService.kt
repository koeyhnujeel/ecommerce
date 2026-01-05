package com.zunza.ecommerce.application.onboarding.service

import com.zunza.ecommerce.application.onboarding.provided.SellerApplicationRegister
import com.zunza.ecommerce.application.onboarding.required.SellerApplicationRepository
import com.zunza.ecommerce.application.onboarding.service.dto.command.SubmitCommand
import com.zunza.ecommerce.application.onboarding.service.dto.result.SubmitResult
import com.zunza.ecommerce.domain.onboarding.DuplicateBusinessNumberException
import com.zunza.ecommerce.domain.onboarding.SellerApplication
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SellerApplicationRegistrationService(
    private val sellerApplicationRepository: SellerApplicationRepository,
) : SellerApplicationRegister {
    override fun submit(command: SubmitCommand): SubmitResult {
        duplicateBusinessNumber(command.businessNumber)

        val sellerApplication = SellerApplication.submit(
            accountId = command.accountId,
            representativeName = command.representativeName,
            contactEmail = command.contactEmail,
            representativePhone = command.representativePhone,
            businessNumber = command.businessNumber,
            companyName = command.companyName,
            brandNameKor = command.brandNameKor,
            brandNameEng = command.brandNameEng,
            brandDescription = command.brandDescription,
            bankName = command.bankName,
            accountNumber = command.accountNumber,
            accountHolder = command.accountHolder,
        )

        sellerApplicationRepository.save(sellerApplication)

        return SubmitResult.from(sellerApplication.id)
    }

    private fun duplicateBusinessNumber(businessNumber: String) {
        if (sellerApplicationRepository.existsByBusinessNumber(businessNumber)) {
            throw DuplicateBusinessNumberException()
        }
    }
}