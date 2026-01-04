package com.zunza.ecommerce.application.fixture

import com.zunza.ecommerce.application.customer.service.dto.command.RegisterShippingAddressCommand
import com.zunza.ecommerce.application.customer.service.dto.command.UpdateShippingAddressCommand

object AddressCommandFixture {
    fun createRegisterAddressCommand(accountId: Long): RegisterShippingAddressCommand {
        return RegisterShippingAddressCommand(
            accountId = accountId,
            alias = "집",
            roadAddress = "서울특별시 관악구 관악로 1",
            detailAddress = "",
            receiverName = "홍길동",
            zipcode = "11111",
            isDefault = true
        )
    }

    fun createUpdateAddressCommand(accountId: Long): UpdateShippingAddressCommand {
        return UpdateShippingAddressCommand(
            accountId = accountId,
            addressId = 1L,
            alias = "집",
            roadAddress = "서울특별시 관악구 관악로 1",
            detailAddress = "",
            receiverName = "홍길동",
            zipcode = "11111",
            isDefault = true
        )
    }
}