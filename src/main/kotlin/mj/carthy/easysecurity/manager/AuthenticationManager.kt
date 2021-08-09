package mj.carthy.easysecurity.manager

import kotlinx.coroutines.reactor.mono
import mj.carthy.easysecurity.authentication.UserTokenAuth
import mj.carthy.easysecurity.service.AuthenticateService
import mj.carthy.easyutils.helper.string
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import reactor.core.publisher.Mono

class AuthenticationManager(private val authenticateService: AuthenticateService): ReactiveAuthenticationManager {
  override fun authenticate(
    authentication: Authentication
  ): Mono<Authentication> = Mono.justOrEmpty(
    authentication.credentials.string
  ).switchIfEmpty(Mono.empty()).map { mono { authenticateService.tokenToUserAuth(
    it
  ) } }.flatMap {
    it
  }.map { UserTokenAuth(
    it,
    authentication.credentials.string
  ) }
}