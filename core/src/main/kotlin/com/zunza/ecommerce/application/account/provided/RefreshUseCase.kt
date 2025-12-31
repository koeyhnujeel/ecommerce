package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.application.account.service.dto.result.RefreshResult

interface RefreshUseCase {
    fun refresh(refreshToken: String): RefreshResult
}