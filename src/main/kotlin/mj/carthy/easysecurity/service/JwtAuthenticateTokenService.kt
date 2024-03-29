package mj.carthy.easysecurity.service

import com.google.common.annotations.VisibleForTesting
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm.HS512
import kotlinx.coroutines.reactive.awaitFirstOrNull
import mj.carthy.easysecurity.document.RoboCop
import mj.carthy.easysecurity.enums.Sex
import mj.carthy.easysecurity.jwtconfiguration.JwtSecurityProperties
import mj.carthy.easysecurity.model.Token
import mj.carthy.easysecurity.model.UserAuth
import mj.carthy.easyutils.helper.string
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.Instant.now
import java.time.temporal.ChronoUnit
import java.util.*

class JwtAuthenticateTokenService(
    /* PROPERTIES */
    private val jwtSecurityProperties: JwtSecurityProperties,
    /* REPOSITORIES */
    private val mongoTemplate: ReactiveMongoTemplate
) {
    companion object {
        /* PARAMS */
        const val SESSION_ID = "sessionId"
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

    suspend fun tokenToUserAuth(token: String): UserAuth? = with(
        Jwts.parser().setSigningKey(jwtSecurityProperties.signingKey).parseClaimsJws(token).body
    ) {
        val sessionId: UUID = UUID.fromString(this.get(SESSION_ID, String::class.java))

        val query = Query().addCriteria(Criteria.where(MAPPED_ID_PARAM).`is`(sessionId))

        if (mongoTemplate.find(query, RoboCop::class.java).awaitFirstOrNull() != null) return null

        val id = UUID.fromString(this.subject)
        val username: String = this.get(USERNAME, String::class.java)
        val sex: Sex = Sex.valueOf(this.get(SEX, String::class.java))
        val accountNonExpired: Boolean = this.get(ACCOUNT_NON_EXPIRED, Object::class.java).string.toBoolean()
        val accountNonLocked: Boolean = this.get(ACCOUNT_NON_LOCKED, Object::class.java).string.toBoolean()
        val credentialsNonExpired: Boolean = this.get(CREDENTIALS_NON_EXPIRED, Object::class.java).string.toBoolean()
        val enable: Boolean = this.get(ENABLE, Object::class.java).string.toBoolean()
        val roles: MutableSet<*> = this.get(ROLES, MutableList::class.java).toMutableSet()
        val authorities: MutableSet<GrantedAuthority> = roles.map { it as String }.map { SimpleGrantedAuthority(it) }.toMutableSet()

        return UserAuth(id, sex, username, authorities, accountNonExpired, accountNonLocked, credentialsNonExpired, enable)
    }

    fun tokenCreator(user: UserAuth, validity: Long = jwtSecurityProperties.validity, unit: ChronoUnit = jwtSecurityProperties.unit): Token {
        val id: UUID = user.id
        val roles = user.authorities.map { it.authority }.toSet()
        val timeout = now().plus(validity, unit)
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
            timeout
        )).compact(), timeout)
    }

    @VisibleForTesting fun getClaims(
        id: UUID,
        user: UserAuth,
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