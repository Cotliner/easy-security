package mj.carthy.easysecurity.configuration

import mj.carthy.easysecurity.manager.AuthenticationManager
import mj.carthy.easysecurity.repositories.SecurityContextRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@ConditionalOnProperty(value = ["security.web.enabled"], matchIfMissing = true)
class DefaultServiceSecurityConfiguration: BaseSecurityConfiguration() {
  override fun securityWebFilterChain(
    authenticationManager: AuthenticationManager,
    securityContextRepository: SecurityContextRepository,
    http: ServerHttpSecurity
  ): SecurityWebFilterChain = super.securityWebFilterChain(authenticationManager, securityContextRepository, http
    .requestCache()
    .disable()
    .cors()
    .disable()
    .headers()
    .disable()
    .authenticationManager(
      authenticationManager
  ).securityContextRepository(
    securityContextRepository
  ).authorizeExchange().anyExchange().authenticated().and())
}