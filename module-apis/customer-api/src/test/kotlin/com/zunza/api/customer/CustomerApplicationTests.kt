package com.zunza.api.customer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

// @SpringBootApplication
@SpringBootApplication(scanBasePackages = ["com.zunza"])
@EntityScan(basePackages = ["com.zunza.domain.entity"])
@EnableJpaRepositories(basePackages = ["com.zunza.domain.repository"])
class CustomerApplicationTests
