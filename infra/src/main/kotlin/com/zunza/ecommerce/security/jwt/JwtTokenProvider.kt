package com.zunza.ecommerce.security.jwt

import com.zunza.ecommerce.domain.enums.UserType
import com.zunza.ecommerce.port.TokenProvider
import com.zunza.ecommerce.support.exception.CustomTokenException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret-key}")
    private val secretKey: String,
    @Value("\${jwt.access-token-expire-time}")
    private val accessTokenExpireTime: Long,
    @Value("\${jwt.refresh-token-expire-time}")
    private val refreshTokenExpireTime: Long,
) : TokenProvider {
    override fun generateAccessToken(
        userId: Long,
        userType: UserType,
    ): String {
        val now = Instant.now()
        return Jwts
            .builder()
            .subject(userId.toString())
            .claim("role", userType.toString())
            .claim("jti", UUID.randomUUID().toString())
            .issuedAt(Date(now.toEpochMilli()))
            .expiration(Date(now.plusMillis(accessTokenExpireTime).toEpochMilli()))
            .signWith(getKey(), Jwts.SIG.HS256)
            .compact()
    }

    override fun generateRefreshToken(userId: Long): String {
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

    override fun validateToken(token: String): Boolean {
        try {
            Jwts.parser()
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

    override fun getUserId(token: String): Long {
        val claims = parseClaims(token)
        return claims.subject.toLong()
    }

    override fun getUserRole(token: String): String {
        val claims = parseClaims(token)
        return claims["role"] as String
    }

    override fun getJti(token: String): String {
        val claims = parseClaims(token)
        return claims["jti"] as String
    }

    override fun getRemainingTime(token: String): Long {
        val expiration = getTokenExpiration(token)
        val now = Date()
        return maxOf(0, expiration.time - now.time)
    }

    private fun getTokenExpiration(token: String): Date {
        return parseClaims(token).expiration
    }

    private fun parseClaims(token: String): Claims {
        return try {
            Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: ExpiredJwtException) {
            e.claims
        }
    }

    private fun getKey(): SecretKey =
        Keys.hmacShaKeyFor(this.secretKey.toByteArray())
}
