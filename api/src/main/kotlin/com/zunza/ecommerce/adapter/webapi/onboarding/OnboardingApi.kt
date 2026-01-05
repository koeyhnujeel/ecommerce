package com.zunza.ecommerce.adapter.webapi.onboarding

import com.zunza.ecommerce.adapter.ApiResponse
import com.zunza.ecommerce.adapter.webapi.onboarding.dto.request.RejectRequest
import com.zunza.ecommerce.adapter.webapi.onboarding.dto.request.SubmitRequest
import com.zunza.ecommerce.adapter.webapi.onboarding.dto.response.CheckApplicationStatusResponse
import com.zunza.ecommerce.adapter.webapi.onboarding.dto.response.SubmitResponse
import com.zunza.ecommerce.application.onboarding.provided.*
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/partnerApplications")
class OnboardingApi(
    private val submitUseCase: SubmitUseCase,
    private val startReviewUseCase: StartReviewUseCase,
    private val approveUseCase: ApproveUseCase,
    private val rejectUseCase: RejectUseCase,
    private val checkApplicationStatusUseCase: CheckApplicationStatusUseCase,
) {
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun submit(
        @AuthenticationPrincipal accountId: Long,
        @RequestBody request: SubmitRequest
    ): ApiResponse<SubmitResponse> {
        val result = submitUseCase.submit(request.toCommand(accountId))

        return ApiResponse.success(SubmitResponse.from(result.partnerApplicationId))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{partnerApplicationId}/review")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun startReview(
        @PathVariable partnerApplicationId: Long,
    ): ApiResponse<Any> {
        startReviewUseCase.startReview(partnerApplicationId)

        return ApiResponse.success()
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{partnerApplicationId}/approval")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun approve(
        @PathVariable partnerApplicationId: Long,
    ): ApiResponse<Any> {
        approveUseCase.approve(partnerApplicationId)

        return ApiResponse.success()
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{partnerApplicationId}/rejection")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun reject(
        @PathVariable partnerApplicationId: Long,
        @RequestBody request: RejectRequest
    ): ApiResponse<Any> {
        rejectUseCase.reject(request.toCommand(partnerApplicationId))

        return ApiResponse.success()
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping
    fun checkApplicationStatus(
        @AuthenticationPrincipal accountId: Long,
    ): ApiResponse<CheckApplicationStatusResponse> {
        val result = checkApplicationStatusUseCase.checkApplicationStatus(accountId)

        return ApiResponse.success(CheckApplicationStatusResponse.from(result))
    }
}