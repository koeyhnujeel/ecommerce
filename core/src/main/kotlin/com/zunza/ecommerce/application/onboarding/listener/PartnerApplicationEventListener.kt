package com.zunza.ecommerce.application.onboarding.listener

import com.zunza.ecommerce.application.account.provided.GrantPartnerRoleUseCase
import com.zunza.ecommerce.application.brand.provided.RegisterBrandUseCase
import com.zunza.ecommerce.application.brand.service.dto.command.RegisterBrandCommand
import com.zunza.ecommerce.application.partner.provided.RegisterPartnerUseCase
import com.zunza.ecommerce.application.partner.service.dto.command.RegisterPartnerCommand
import com.zunza.ecommerce.domain.onboarding.event.PartnerApplicationApprovedEvent
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class PartnerApplicationEventListener(
    private val grantPartnerRoleUseCase: GrantPartnerRoleUseCase,
    private val registerPartnerUseCase: RegisterPartnerUseCase,
    private val registerBrandUseCase: RegisterBrandUseCase
) {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleApproved(event: PartnerApplicationApprovedEvent) {
        grantPartnerRoleUseCase.grantPartnerRole(event.accountId)

        val partnerId = registerPartnerUseCase.registerPartner(RegisterPartnerCommand.from(event))

        registerBrandUseCase.registerBrand(RegisterBrandCommand.of(partnerId, event.brandInfo))
    }
}