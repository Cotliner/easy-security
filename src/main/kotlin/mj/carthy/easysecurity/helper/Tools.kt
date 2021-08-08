package mj.carthy.easysecurity.helper

import io.jsonwebtoken.Claims
import mj.carthy.easysecurity.enums.Sex
import mj.carthy.easysecurity.model.UserAuth
import mj.carthy.easysecurity.service.AuthenticateService
import mj.carthy.easyutils.exception.UnprocessedException
import mj.carthy.easyutils.helper.Errors.Companion.PROPERTY_NOT_FOUND
import mj.carthy.easyutils.helper.string
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.*

const val UNKNOWN_SEX = "Can not inverse unknown sex"

fun Sex.inversed(): Sex = when(this) {
  Sex.MALE -> Sex.FEMALE
  Sex.FEMALE -> Sex.MALE
  Sex.UNKNOWN -> throw UnprocessedException(PROPERTY_NOT_FOUND, UNKNOWN_SEX)
}

fun Claims.toUserAuth(): UserAuth {

  val id: UUID = UUID.fromString(this.get(AuthenticateService.ID, String::class.java))
  val username: String = this.get(AuthenticateService.USERNAME, String::class.java)
  val sex: Sex = Sex.valueOf(this.get(AuthenticateService.SEX, String::class.java))
  val accountNonExpired: Boolean = this.get(AuthenticateService.ACCOUNT_NON_EXPIRED, Object::class.java).string.toBoolean()
  val accountNonLocked: Boolean = this.get(AuthenticateService.ACCOUNT_NON_LOCKED, Object::class.java).string.toBoolean()
  val credentialsNonExpired: Boolean = this.get(AuthenticateService.CREDENTIALS_NON_EXPIRED, Object::class.java).string.toBoolean()
  val enable: Boolean = this.get(AuthenticateService.ENABLE, Object::class.java).string.toBoolean()
  val roles: MutableSet<*> = this.get(AuthenticateService.ROLES, MutableList::class.java).toMutableSet()
  val authorities: MutableSet<GrantedAuthority> = roles.map { it as String }.map { SimpleGrantedAuthority(it) }.toMutableSet()

  return UserAuth(id, sex, username, authorities, accountNonExpired, accountNonLocked, credentialsNonExpired, enable)
}