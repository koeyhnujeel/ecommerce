package com.zunza.ecommerce.application.fixture

import com.zunza.ecommerce.application.brand.service.dto.command.RegisterBrandCommand
import com.zunza.ecommerce.domain.brand.BrandInfo

object RegisterBrandCommandFixture {

    fun createRegisterBrandCommand(
        partnerId: Long = 1L,
        nameKor: String = "나이키",
        nameEng: String = "Nike",
        description: String = "Just Do It",
    ) = RegisterBrandCommand.of(
        partnerId = partnerId,
        brandInfo = BrandInfo(
            nameKor = nameKor,
            nameEng = nameEng,
            description = description,
        )
    )
}
