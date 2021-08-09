package mj.carthy.easysecurity.service

import com.google.common.annotations.VisibleForTesting
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm.HS512
import kotlinx.coroutines.reactive.awaitFirstOrNull
import mj.carthy.easysecurity.document.RoboCop
import mj.carthy.easysecurity.helper.toUserAuth
import mj.carthy.easysecurity.jwtconfiguration.SecurityProperties
import mj.carthy.easysecurity.model.Token
import mj.carthy.easysecurity.model.UserAuth
import mj.carthy.easyutils.helper.string
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import java.time.Instant
import java.time.Instant.now
import java.time.temporal.ChronoUnit
import java.util.*

class AuthenticateService(
  /* PROPERTIES */
  private val securityProperties: SecurityProperties,
  /* REPOSITORIES */
  private val mongoTemplate: ReactiveMongoTemplate
) {
  companion object {
    /* PARAMS */
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

  suspend fun tokenToUserAuth(token: String, signKey: String = securityProperties.signKey): UserAuth? = with(
    Jwts.parser().setSigningKey(signKey).parseClaimsJws(token).body
  ) {
    val sessionId: UUID = UUID.fromString(this.subject)

    val query = Query().addCriteria(Criteria.where(MAPPED_ID_PARAM).`is`(sessionId))

    if (mongoTemplate.find(query, RoboCop::class.java).awaitFirstOrNull() != null) return null

    return toUserAuth()
  }

  fun tokenCreator(sessionId: UUID, user: UserAuth, amount: Long, unit: ChronoUnit, signKey: String): Token {
    val roles = user.authorities.map { it.authority }.toSet()
    val limit: Instant = now().plus(amount, unit)
    return Token(Jwts.builder().signWith(
      HS512,
      signKey
    ).setClaims(getClaims(
      user,
      roles
    )).setSubject(
      sessionId.string
    ).setIssuedAt(Date.from(
      now()
    )).setExpiration(Date.from(limit)).compact(), limit)
  }

  @VisibleForTesting fun getClaims(
    user: UserAuth,
    roles: Set<String>
  ): Map<String, Any> = with(HashMap<String, Any>()) {
    this[ID] = user.id
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