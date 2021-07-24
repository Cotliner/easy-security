package mj.carthy.easysecurity.service

import com.google.common.annotations.VisibleForTesting
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm.HS512
import mj.carthy.easysecurity.jwtconfiguration.JwtSecurityProperties
import mj.carthy.easyutils.enums.Sex
import mj.carthy.easyutils.helper.string
import mj.carthy.easyutils.model.Token
import mj.carthy.easyutils.model.UserSecurity
import org.apache.commons.lang3.StringUtils.EMPTY
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import java.time.Instant.now
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

    fun createUserSecurityFromToken(
        token: String
    ): UserSecurity = with(Jwts.parser().setSigningKey(jwtSecurityProperties.signingKey).parseClaimsJws(token).body) {
        val id = UUID.fromString(this.subject)
        val username: String = this.get(USERNAME, String::class.java)
        val sex: Sex = Sex.valueOf(this.get(SEX, String::class.java))
        val accountNonExpired: Boolean = this.get(ACCOUNT_NON_EXPIRED, Object::class.java).string.toBoolean()
        val accountNonLocked: Boolean = this.get(ACCOUNT_NON_LOCKED, Object::class.java).string.toBoolean()
        val credentialsNonExpired: Boolean = this.get(CREDENTIALS_NON_EXPIRED, Object::class.java).string.toBoolean()
        val enable: Boolean = this.get(ENABLE, Object::class.java).string.toBoolean()
        val roles: MutableSet<*> = this.get(ROLES, MutableList::class.java).toMutableSet()
        val authorities: MutableSet<GrantedAuthority> = roles.map { it as String }.map { SimpleGrantedAuthority(it) }.toMutableSet()
        return UserSecurity(id, sex, username, EMPTY, authorities, accountNonExpired, accountNonLocked, credentialsNonExpired, enable)
    }

    fun createToken(id: UUID, user: UserSecurity): Token {
        val roles = user.authorities.map { it.authority }.toSet()
        val expiryTime = now().plus(jwtSecurityProperties.validity, jwtSecurityProperties.unit)

        val token: String = Jwts.builder().signWith(HS512, jwtSecurityProperties.signingKey)
                .setClaims(getClaims(id, user, roles))
                .setSubject(id.string)
                .setIssuedAt(Date.from(now()))
                .setExpiration(Date.from(expiryTime))
                .compact()

        return Token(token, expiryTime)
    }

    @VisibleForTesting fun getClaims(
        id: UUID,
        user: UserSecurity,
        roles: Set<String>
    ): Map<String, Any> = with(HashMap<String, Any>()) {
        this[ID] = id
        this[USERNAME] = user.username
        this[SEX] = user.sex
        this[ROLES] = roles
        this[ACCOUNT_NON_EXPIRED] = user.isAccountNonExpired
        this[ACCOUNT_NON_LOCKED] = user.isAccountNonLocked
        this[CREDENTIALS_NON_EXPIRED] = user.isCredentialsNonExpired
        this[ENABLE] = user.isEnabled
        this[TOKEN_CREATE_TIME] = now().truncatedTo(MINUTES).string
        return this
    }
}