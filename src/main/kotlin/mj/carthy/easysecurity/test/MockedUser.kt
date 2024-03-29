package mj.carthy.easysecurity.test

import mj.carthy.easysecurity.enums.Sex
import mj.carthy.easysecurity.enums.Sex.UNKNOWN
import org.springframework.security.test.context.support.WithSecurityContext

@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = UserAuthMockFactory::class)
annotation class MockedUser(
  val id: String = "24aa766d-2a2f-4649-b870-b88cca33bd76",
  val username: String = "john.doe@yopmail.com",
  val sex: Sex = UNKNOWN,
  val role: String = "ADMIN",
  val accountNonExpired: Boolean = true,
  val accountNonLocked: Boolean = true,
  val credentialsNonExpired: Boolean = true,
  val enabled: Boolean = true
)