package com.zunza.ecommerce.application.product.service

import com.zunza.ecommerce.application.product.provided.ProductRegister
import com.zunza.ecommerce.application.product.required.ProductRepository
import com.zunza.ecommerce.application.product.service.dto.command.RegisterProductCommand
import com.zunza.ecommerce.application.stock.provided.StockRegister
import com.zunza.ecommerce.application.stock.service.command.RegisterStockCommand
import com.zunza.ecommerce.domain.product.Product
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductRegistrationService(
    private val productRepository: ProductRepository,
    private val stockRegister: StockRegister
) : ProductRegister {
    @Transactional
    override fun register(command: RegisterProductCommand): Long {
        val product = Product.register(command.toSpec())

        productRepository.save(product)

        registerStocks(command, product)

        return product.id
    }

    private fun registerStocks(
        command: RegisterProductCommand,
        product: Product
    ) {
        val quantityMap = command.options.associate { optionCommand ->
            (optionCommand.color to optionCommand.size) to optionCommand.quantity
        }

        product.productOptions.forEach { option ->
            val command = RegisterStockCommand(option.id, quantityMap[option.color to option.size]!!)
            stockRegister.register(command)
        }
    }
}