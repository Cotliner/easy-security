package mj.carthy.easysecurity.repositories

import mj.carthy.easysecurity.manager.AuthenticationManager
import mj.carthy.easysecurity.tools.extract
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class SecurityContextRepository(private val authenticationManager: AuthenticationManager): ServerSecurityContextRepository {

    override fun save(severWebExchange: ServerWebExchange, securityContext: SecurityContext) = throw NotImplementedError()

    override fun load(
      serverWebExchange: ServerWebExchange
    ): Mono<SecurityContext> = Mono.justOrEmpty(serverWebExchange.extract(
      AUTHORIZATION
    )).switchIfEmpty(Mono.empty()).map { UsernamePasswordAuthenticationToken(
      null,
      it
    ) }.flatMap { auth -> authenticationManager.authenticate(auth).map { SecurityContextImpl(it) } }
}