package com.zunza.ecommerce.adapter.security.jwt

import com.zunza.ecommerce.application.auth.exception.CustomTokenException
import com.zunza.ecommerce.application.auth.required.TokenProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver

class JwtAuthenticationFilter(
    private val tokenProvider: TokenProvider,
    private val resolver: HandlerExceptionResolver,
) : OncePerRequestFilter() {
    companion object {
        private const val BEARER = "Bearer "
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val token = getTokenFromCookie(request)

            if (!token.isNullOrBlank()) {
                tokenProvider.validateToken(token)

                val authentication = getAuthentication(token)
                SecurityContextHolder.getContext().authentication = authentication
            }

            filterChain.doFilter(request, response)

        } catch (e: CustomTokenException) {
            resolver.resolveException(request, response, null, e)
        }
    }

    private fun getAuthentication(token: String): Authentication {
        return UsernamePasswordAuthenticationToken(
            tokenProvider.getAccountId(token),
            null,
            listOf(SimpleGrantedAuthority(tokenProvider.getAccountRole(token)))
        )
    }

    private fun getTokenFromCookie(request: HttpServletRequest): String? {
        return request.cookies
            ?.firstOrNull { it.name == "accessToken" }
            ?.value
    }
}