package mj.carthy.easysecurity.helper

import com.google.common.annotations.VisibleForTesting
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import mj.carthy.easysecurity.enums.Sex
import mj.carthy.easysecurity.model.Token
import mj.carthy.easysecurity.model.UserAuth
import mj.carthy.easyutils.exception.UnprocessedException
import mj.carthy.easyutils.helper.Errors.Companion.PROPERTY_NOT_FOUND
import mj.carthy.easyutils.helper.string
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.Instant
import java.time.Instant.now
import java.time.temporal.ChronoUnit
import java.util.*

/* TOKEN PARAMS */
const val ID = "id"
const val USERNAME = "username"
const val SEX = "sex"
const val ACCOUNT_NON_EXPIRED = "accountNonExpired"
const val ACCOUNT_NON_LOCKED = "accountNonLocked"
const val CREDENTIALS_NON_EXPIRED = "credentialsNonExpired"
const val ENABLE = "enable"
const val ROLES = "roles"

/* OTHER PARAMS */
const val UNKNOWN_SEX = "Can not inverse unknown sex"

fun Sex.inversed(): Sex = when(this) {
  Sex.MALE -> Sex.FEMALE
  Sex.FEMALE -> Sex.MALE
  Sex.UNKNOWN -> throw UnprocessedException(PROPERTY_NOT_FOUND, UNKNOWN_SEX)
}

fun Claims.toUserAuth(): UserAuth {
  val id: UUID = UUID.fromString(this.get(ID, String::class.java))
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

fun tokenCreator(sessionId: UUID, user: UserAuth, deadline: Instant, signKey: String): Token {
  val roles = user.authorities.map { it.authority }.toSet()
  return Token(Jwts.builder().signWith(
    SignatureAlgorithm.HS512,
    signKey
  ).setClaims(getClaims(
    user,
    roles
  )).setSubject(
    sessionId.string
  ).setIssuedAt(Date.from(
    now()
  )).setExpiration(Date.from(deadline)).compact(), deadline)
}

fun getClaims(user: UserAuth, roles: Set<String>): Map<String, Any> = with(HashMap<String, Any>()) {
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

fun UserAuth.isAllow(authority: Set<GrantedAuthority>): Boolean = this.authorities.any { it in authority }
val UserAuth.isAdmin get(): Boolean = this.authorities.stream().map { it as GrantedAuthority }.map { it.authority }.anyMatch { "ADMIN" == it }
