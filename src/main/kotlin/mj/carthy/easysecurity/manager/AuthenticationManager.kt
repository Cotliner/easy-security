package mj.carthy.easysecurity.manager

import kotlinx.coroutines.reactor.mono
import mj.carthy.easysecurity.authentication.UserTokenAuthentication
import mj.carthy.easysecurity.service.JwtAuthenticateTokenService
import mj.carthy.easyutils.helper.string
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import reactor.core.publisher.Mono
import java.util.*

class AuthenticationManager(
  private val jwtAuthenticateTokenService: JwtAuthenticateTokenService,
  private val sessionGetter: (suspend (sessionId: UUID) -> Mono<out Any>)? = null
): ReactiveAuthenticationManager {
  override fun authenticate(
    authentication: Authentication
  ): Mono<Authentication> = Mono.justOrEmpty(
    authentication.credentials.string
  ).switchIfEmpty(Mono.empty()).map { mono { jwtAuthenticateTokenService.createUserSecurityFromToken(
    it,
    sessionGetter
  ) } }.flatMap {
    it
  }.map { UserTokenAuthentication(
    it,
    authentication.credentials.string
  ) }
}