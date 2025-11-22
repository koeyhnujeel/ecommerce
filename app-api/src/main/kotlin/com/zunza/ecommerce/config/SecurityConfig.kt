package com.zunza.ecommerce.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.zunza.ecommerce.port.TokenProvider
import com.zunza.ecommerce.repository.TokenBlacklistRepository
import com.zunza.ecommerce.security.jwt.filter.JwtAuthenticationFilter
import com.zunza.ecommerce.security.jwt.filter.JwtExceptionFilter
import com.zunza.ecommerce.security.jwt.handler.JwtAccessDeniedHandler
import com.zunza.ecommerce.security.jwt.handler.JwtAuthenticationEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val objectMapper: ObjectMapper,
    private val tokenProvider: TokenProvider,
    private val tokenBlacklistRepository: TokenBlacklistRepository
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { authorize ->
                authorize.anyRequest().permitAll()
            }
            .addFilterBefore(
                JwtExceptionFilter(objectMapper),
                UsernamePasswordAuthenticationFilter::class.java,
            )
            .addFilterBefore(
                JwtAuthenticationFilter(tokenProvider, tokenBlacklistRepository),
                UsernamePasswordAuthenticationFilter::class.java,
            )
            .exceptionHandling { exception ->
                exception
                    .authenticationEntryPoint(JwtAuthenticationEntryPoint(objectMapper))
                    .accessDeniedHandler(JwtAccessDeniedHandler(objectMapper))
            }

        return http.build()
    }
}
