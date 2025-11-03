package com.zunza.api.customer.config

import com.redis.testcontainers.RedisContainer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class TestContainersConfig {
    @Bean
    @ServiceConnection
    fun mysqlContainer(): PostgreSQLContainer<*> =
        PostgreSQLContainer(DockerImageName.parse("postgres:17.6-alpine"))
            .withReuse(true)

    @Bean
    @ServiceConnection
    fun redisContainer(): RedisContainer =
        RedisContainer(DockerImageName.parse("redis:7-alpine"))
            .withReuse(true)
}
