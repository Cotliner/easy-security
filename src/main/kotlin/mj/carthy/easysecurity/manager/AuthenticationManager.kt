package mj.carthy.easysecurity.manager

import io.jsonwebtoken.Claims
import kotlinx.coroutines.reactor.mono
import mj.carthy.easysecurity.authentication.UserTokenAuth
import mj.carthy.easysecurity.model.UserAuth
import mj.carthy.easysecurity.service.AuthenticateService
import mj.carthy.easyutils.helper.string
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import reactor.core.publisher.Mono
import java.util.*

class AuthenticationManager(private val authenticateService: AuthenticateService): ReactiveAuthenticationManager {
  override fun authenticate(
    authentication: Authentication
  ): Mono<Authentication> = Mono.justOrEmpty(
    authentication.credentials.string
  ).switchIfEmpty(Mono.empty()).map { mono {
    val claims: Claims = authenticateService.tokenParser(it)
    val sessionId: UUID = authenticateService.sessionId(claims)
    val userAuth: UserAuth = authenticateService.tokenToUserAuth(claims, sessionId) ?: return@mono null
    UserTokenAuth(userAuth, authentication.credentials.string, sessionId)
  } }.flatMap { it }
}