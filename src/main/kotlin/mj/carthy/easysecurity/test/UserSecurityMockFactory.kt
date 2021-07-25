package mj.carthy.easysecurity.test

import mj.carthy.easysecurity.model.UserSecurity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory
import java.util.*

class UserSecurityMockFactory: WithSecurityContextFactory<MockedUser> {

  override fun createSecurityContext(
    customUser: MockedUser
  ): SecurityContext {
    val context: SecurityContext = SecurityContextHolder.createEmptyContext()
    val principal: UserSecurity = createUserSecurity(customUser)
    val auth: Authentication = UsernamePasswordAuthenticationToken(principal, null, principal.authorities)
    context.authentication = auth
    return context
  }

  private fun createUserSecurity(customUser: MockedUser): UserSecurity = UserSecurity(
    UUID.fromString(customUser.id),
    customUser.sex,
    customUser.username,
    customUser.password,
    mutableSetOf(SimpleGrantedAuthority(customUser.role)),
    customUser.accountNonExpired,
    customUser.accountNonLocked,
    customUser.credentialsNonExpired,
    customUser.enabled
  )
}