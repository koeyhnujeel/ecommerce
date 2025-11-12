package com.zunza.auth.jwt

import com.zunza.common.support.exception.CustomTokenException
import com.zunza.domain.enums.UserRole
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

@Component
class JwtProvider(
    @Value("\${jwt.secret-key}")
    private val secretKey: String,
    @Value("\${jwt.access-token-expire-time}")
    private val accessTokenExpireTime: Long,
    @Value("\${jwt.refresh-token-expire-time}")
    private val refreshTokenExpireTime: Long,
) {
    fun generateAccessToken(
        userId: Long,
        userRole: UserRole,
    ): String {
        val now = Instant.now()
        return Jwts
            .builder()
            .subject(userId.toString())
            .claim("role", userRole.toString())
            .claim("jti", UUID.randomUUID().toString())
            .issuedAt(Date(now.toEpochMilli()))
            .expiration(Date(now.plusMillis(accessTokenExpireTime).toEpochMilli()))
            .signWith(getKey(), Jwts.SIG.HS256)
            .compact()
    }

    fun generateRefreshToken(userId: Long): String {
        val now = Instant.now()
        return Jwts
            .builder()
            .subject(userId.toString())
            .claim("jti", UUID.randomUUID().toString())
            .issuedAt(Date(now.toEpochMilli()))
            .expiration(Date(now.plusMillis(refreshTokenExpireTime).toEpochMilli()))
            .signWith(getKey(), Jwts.SIG.HS256)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        try {
            Jwts
                .parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
            return true
        } catch (e: ExpiredJwtException) {
            throw CustomTokenException("토큰이 만료되었습니다.")
        } catch (e: SignatureException) {
            throw CustomTokenException("토큰 서명이 올바르지 않습니다.")
        } catch (e: MalformedJwtException) {
            throw CustomTokenException("잘못된 형식의 토큰입니다.")
        }
    }

    fun getAuthentication(token: String): Authentication {
        val claims = parseClaims(token)
        val userId = claims.subject
        val role = claims["role"] as String

        return UsernamePasswordAuthenticationToken(
            userId.toLong(),
            null,
            listOf(SimpleGrantedAuthority(role)),
        )
    }

    fun parseClaims(token: String): Claims =
        try {
            Jwts
                .parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: ExpiredJwtException) {
            e.claims
        }

    private fun getKey(): SecretKey = Keys.hmacShaKeyFor(this.secretKey.toByteArray())
}
