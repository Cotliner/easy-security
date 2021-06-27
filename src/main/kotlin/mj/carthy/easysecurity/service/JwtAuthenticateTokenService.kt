package mj.carthy.easysecurity.service

import com.google.common.annotations.VisibleForTesting
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm.HS512
import mj.carthy.easysecurity.jwtconfiguration.JwtSecurityProperties
import mj.carthy.easyutils.enums.Sex
import mj.carthy.easyutils.model.Token
import mj.carthy.easyutils.model.UserSecurity
import org.apache.commons.lang3.StringUtils.EMPTY
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit.MINUTES
import java.util.*

@Service class JwtAuthenticateTokenService(val jwtSecurityProperties: JwtSecurityProperties) {
    companion object {
        const val ID = "id"
        const val USERNAME = "username"
        const val SEX = "sex"
        const val ACCOUNT_NON_EXPIRED = "accountNonExpired"
        const val ACCOUNT_NON_LOCKED = "accountNonLocked"
        const val CREDENTIALS_NON_EXPIRED = "credentialsNonExpired"
        const val ENABLE = "enable"
        const val TOKEN_CREATE_TIME = "TOKEN_CREATE_TIME"
        const val ROLES = "roles"
    }

    fun createUserSecurityFromToken(token: String): UserSecurity {
        val claimsJws: Jws<Claims> = Jwts.parser().setSigningKey(jwtSecurityProperties.signingKey).parseClaimsJws(token)
        val body: Claims = claimsJws.body
        val id = UUID.fromString(body.subject)
        val username: String = body.get(USERNAME, String::class.java)
        val sex: Sex = Sex.valueOf(body.get(SEX, String::class.java))
        val accountNonExpired: Boolean = body.get(ACCOUNT_NON_EXPIRED, Object::class.java).toString().toBoolean()
        val accountNonLocked: Boolean = body.get(ACCOUNT_NON_LOCKED, Object::class.java).toString().toBoolean()
        val credentialsNonExpired: Boolean = body.get(CREDENTIALS_NON_EXPIRED, Object::class.java).toString().toBoolean()
        val enable: Boolean = body.get(ENABLE, Object::class.java).toString().toBoolean()
        val roles: MutableSet<*> = body.get(ROLES, MutableList::class.java).toMutableSet()
        val authorities: MutableSet<GrantedAuthority> = roles.map { it as String }.map { SimpleGrantedAuthority(it) }.toMutableSet()
        return UserSecurity(id, sex, username, EMPTY, authorities, accountNonExpired, accountNonLocked, credentialsNonExpired, enable)
    }

    fun createToken(id: UUID, user: UserSecurity): Token {
        val roles = user.authorities.map { it.authority }.toSet()
        val expiryTime = Instant.now().plus(jwtSecurityProperties.validity, jwtSecurityProperties.unit)

        val token: String = Jwts.builder().signWith(HS512, jwtSecurityProperties.signingKey)
                .setClaims(getClaims(id, user, roles))
                .setSubject(id.toString())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(expiryTime))
                .compact()

        return Token(token, expiryTime)
    }

    @VisibleForTesting fun getClaims(id: UUID, user: UserSecurity, roles: Set<String>): Map<String, Any> {
        val claims: MutableMap<String, Any> = HashMap()
        claims[ID] = id
        claims[USERNAME] = user.username
        claims[SEX] = user.sex
        claims[ROLES] = roles
        claims[ACCOUNT_NON_EXPIRED] = user.isAccountNonExpired
        claims[ACCOUNT_NON_LOCKED] = user.isAccountNonLocked
        claims[CREDENTIALS_NON_EXPIRED] = user.isCredentialsNonExpired
        claims[ENABLE] = user.isEnabled
        claims[TOKEN_CREATE_TIME] = Instant.now().truncatedTo(MINUTES).toString()
        return claims
    }
}