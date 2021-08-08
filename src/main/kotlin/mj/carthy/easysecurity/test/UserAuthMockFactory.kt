package mj.carthy.easysecurity.test

import mj.carthy.easysecurity.model.UserAuth
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory
import java.util.*

class UserAuthMockFactory: WithSecurityContextFactory<MockedUser> {

  override fun createSecurityContext(
    customUser: MockedUser
  ): SecurityContext {
    val context: SecurityContext = SecurityContextHolder.createEmptyContext()
    val principal: UserAuth = createUserAuth(customUser)
    val auth: Authentication = UsernamePasswordAuthenticationToken(principal, null, principal.authorities)
    context.authentication = auth
    return context
  }

  private fun createUserAuth(user: MockedUser): UserAuth = UserAuth(
    UUID.fromString(user.id),
    user.sex,
    user.username,
    mutableSetOf(SimpleGrantedAuthority(user.role)),
    user.accountNonExpired,
    user.accountNonLocked,
    user.credentialsNonExpired,
    user.enabled
  )
}