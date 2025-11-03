package com.zunza.ecommerce

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["com.zunza"])
@EntityScan(basePackages = ["com.zunza.domain.entity"])
@EnableJpaRepositories(basePackages = ["com.zunza.domain.repository"])
class EcommerceApplication

fun main(args: Array<String>) {
    runApplication<EcommerceApplication>(*args)
}
