package com.zunza.ecommerce.application.onboarding.service

import com.zunza.ecommerce.application.account.provided.GrantPartnerRoleUseCase
import com.zunza.ecommerce.application.brand.provided.RegisterBrandUseCase
import com.zunza.ecommerce.application.onboarding.provided.ApproveUseCase
import com.zunza.ecommerce.application.onboarding.provided.RejectUseCase
import com.zunza.ecommerce.application.onboarding.provided.StartReviewUseCase
import com.zunza.ecommerce.application.onboarding.provided.SubmitUseCase
import com.zunza.ecommerce.application.onboarding.required.PartnerApplicationRepository
import com.zunza.ecommerce.application.onboarding.required.findByIdOrThrow
import com.zunza.ecommerce.application.onboarding.service.dto.command.RejectCommand
import com.zunza.ecommerce.application.onboarding.service.dto.command.SubmitCommand
import com.zunza.ecommerce.application.onboarding.service.dto.result.SubmitResult
import com.zunza.ecommerce.application.partner.provided.RegisterPartnerUseCase
import com.zunza.ecommerce.domain.onboarding.DuplicateBusinessNumberException
import com.zunza.ecommerce.domain.onboarding.PartnerApplication
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PartnerApplicationCommandService(
    private val partnerApplicationRepository: PartnerApplicationRepository,
    private val grantPartnerRoleUseCase: GrantPartnerRoleUseCase,
    private val registerPartnerUseCase: RegisterPartnerUseCase,
    private val registerBrandUseCase: RegisterBrandUseCase
) : SubmitUseCase,
    StartReviewUseCase,
    ApproveUseCase,
    RejectUseCase
{
    override fun submit(command: SubmitCommand): SubmitResult {
        duplicateBusinessNumber(command.businessNumber)

        val partnerApplication = PartnerApplication.submit(
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

        partnerApplicationRepository.save(partnerApplication)

        return SubmitResult.from(partnerApplication.id)
    }

    override fun startReview(partnerApplicationId: Long) {
        val partnerApplication = partnerApplicationRepository.findByIdOrThrow(partnerApplicationId)

        partnerApplication.startReview()

        partnerApplicationRepository.save(partnerApplication)
    }

    override fun approve(partnerApplicationId: Long) {
        val partnerApplication = partnerApplicationRepository.findByIdOrThrow(partnerApplicationId)

        partnerApplication.approve()

        grantPartnerRoleUseCase.grantPartnerRole(partnerApplication.accountId)

        val partnerId = registerPartnerUseCase.registerPartner(
            partnerApplication.accountId,
            partnerApplication.id,
            partnerApplication.businessInfo,
            partnerApplication.settlementAccount
        )

        registerBrandUseCase.registerBrand(partnerId, partnerApplication.brandInfo)
    }

    override fun reject(command: RejectCommand) {
        val partnerApplication = partnerApplicationRepository.findByIdOrThrow(command.partnerApplicationId)

        partnerApplication.reject(command.reason)

        partnerApplicationRepository.save(partnerApplication)
    }

    private fun duplicateBusinessNumber(businessNumber: String) {
        if (partnerApplicationRepository.existsByBusinessNumber(businessNumber)) {
            throw DuplicateBusinessNumberException()
        }
    }
}