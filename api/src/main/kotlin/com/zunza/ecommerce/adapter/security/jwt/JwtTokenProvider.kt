package com.zunza.ecommerce.adapter.security.jwt

import com.zunza.ecommerce.adapter.security.jwt.exception.ExpiredTokenException
import com.zunza.ecommerce.adapter.security.jwt.exception.InvalidSignatureTokenException
import com.zunza.ecommerce.adapter.security.jwt.exception.MalformedTokenException
import com.zunza.ecommerce.adapter.security.jwt.exception.UnsupportedTokenException
import com.zunza.ecommerce.application.account.required.TokenProvider
import com.zunza.ecommerce.domain.account.UserRole
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey

@Component
@EnableConfigurationProperties(JwtProperties::class)
class JwtTokenProvider(
    private val properties: JwtProperties,
) : TokenProvider {
    override fun generateAccessToken(accountId: Long, roles: List<UserRole>): String {
        val now = Instant.now()
        return Jwts
            .builder()
            .subject(accountId.toString())
            .claim("roles", roles)
            .claim("jti", UUID.randomUUID().toString())
            .issuedAt(Date(now.toEpochMilli()))
            .expiration(Date(now.plusMillis(properties.accessTokenTtl).toEpochMilli()))
            .signWith(getKey(), Jwts.SIG.HS256)
            .compact()
    }

    override fun generateRefreshToken(accountId: Long): String {
        val now = Instant.now()
        return Jwts
            .builder()
            .subject(accountId.toString())
            .claim("jti", UUID.randomUUID().toString())
            .issuedAt(Date(now.toEpochMilli()))
            .expiration(Date(now.plusMillis(properties.refreshTokenTtl).toEpochMilli()))
            .signWith(getKey(), Jwts.SIG.HS256)
            .compact()
    }

    override fun validateToken(token: String) {
        try{
            Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
        } catch (e: ExpiredJwtException) {
            throw ExpiredTokenException()
        } catch (e: SignatureException) {
            throw InvalidSignatureTokenException()
        } catch (e: MalformedJwtException) {
            throw MalformedTokenException()
        } catch (e: UnsupportedJwtException) {
            throw UnsupportedTokenException()
        }
    }

    override fun getAccountId(token: String): Long {
        val claims = parseClaims(token)
        return claims.subject.toLong()
    }

    override fun getAccountRoles(token: String): List<String> {
        val claims = parseClaims(token)
        return claims["roles"] as List<String>
    }

    override fun getRemainingTime(token: String): Long {
        val expiration = parseClaims(token).expiration
        val now = Date()

        return maxOf(0, expiration.time - now.time)
    }

    private fun getKey(): SecretKey =
        Keys.hmacShaKeyFor(properties.secret.toByteArray())

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
}