package mj.carthy.easysecurity.service

import com.google.common.annotations.VisibleForTesting
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm.HS512
import kotlinx.coroutines.reactive.awaitFirstOrNull
import mj.carthy.easysecurity.document.Exclude
import mj.carthy.easysecurity.jwtconfiguration.JwtSecurityProperties
import mj.carthy.easysecurity.model.Token
import mj.carthy.easysecurity.model.UserSecurity
import mj.carthy.easyutils.enums.Sex
import mj.carthy.easyutils.helper.string
import org.apache.commons.lang3.StringUtils.EMPTY
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.Instant.now
import java.util.*

class JwtAuthenticateTokenService(
    /* PROPERTIES */
    private val jwtSecurityProperties: JwtSecurityProperties,
    /* REPOSITORIES */
    private val mongoTemplate: ReactiveMongoTemplate
) {
    companion object {
        /* ERRORS */
        const val EXCLUDED_SESSION = "Excluded session"
        /* PARAMS */
        const val SESSION_ID = "SESSION_ID"
        const val ID = "id"
        const val USERNAME = "username"
        const val SEX = "sex"
        const val ACCOUNT_NON_EXPIRED = "accountNonExpired"
        const val ACCOUNT_NON_LOCKED = "accountNonLocked"
        const val CREDENTIALS_NON_EXPIRED = "credentialsNonExpired"
        const val ENABLE = "enable"
        const val ROLES = "roles"
        /* QUERIES PARAM */
        const val MAPPED_ID_PARAM = "mappedId"
    }

    suspend fun createUserSecurityFromToken(
        token: String
    ): UserSecurity = with(
        Jwts.parser().setSigningKey(jwtSecurityProperties.signingKey).parseClaimsJws(token).body
    ) {
        val sessionId: UUID = UUID.fromString(this.get(SESSION_ID, String::class.java))

        val query = Query().addCriteria(Criteria.where(MAPPED_ID_PARAM).`is`(sessionId))

        if (mongoTemplate.find(query, Exclude::class.java).awaitFirstOrNull() != null) throw AccessDeniedException(EXCLUDED_SESSION)

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
        return Token(Jwts.builder().signWith(
            HS512,
            jwtSecurityProperties.signingKey
        ).setClaims(getClaims(id,
            user,
            roles
        )).setSubject(
            id.string
        ).setIssuedAt(Date.from(
            now()
        )).setExpiration(Date.from(
            expiryTime
        )).compact(), expiryTime)
    }

    @VisibleForTesting fun getClaims(
        id: UUID,
        user: UserSecurity,
        roles: Set<String>
    ): Map<String, Any> = with(HashMap<String, Any>()) {
        this[SESSION_ID] = UUID.randomUUID()
        this[ID] = id
        this[USERNAME] = user.username
        this[SEX] = user.sex
        this[ROLES] = roles
        this[ACCOUNT_NON_EXPIRED] = user.isAccountNonExpired
        this[ACCOUNT_NON_LOCKED] = user.isAccountNonLocked
        this[CREDENTIALS_NON_EXPIRED] = user.isCredentialsNonExpired
        this[ENABLE] = user.isEnabled
        return this
    }
}