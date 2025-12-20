package com.zunza.ecommerce.adapter.security.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.zunza.ecommerce.adapter.security.jwt.JwtAuthenticationFilter
import com.zunza.ecommerce.application.auth.required.TokenProvider
import com.zunza.ecommerce.application.auth.required.TokenRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.servlet.HandlerExceptionResolver

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    @param:Qualifier("handlerExceptionResolver")
    private val resolver: HandlerExceptionResolver,
    private val objectMapper: ObjectMapper,
    private val tokenProvider: TokenProvider,
    private val tokenRepository: TokenRepository,
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
                JwtAuthenticationFilter(tokenProvider, tokenRepository, resolver),
                UsernamePasswordAuthenticationFilter::class.java,
            )
//            .exceptionHandling { exception ->
//                exception
//                    .authenticationEntryPoint(JwtAuthenticationEntryPoint(objectMapper))
//                    .accessDeniedHandler(JwtAccessDeniedHandler(objectMapper))
//            }

        return http.build()
    }
}