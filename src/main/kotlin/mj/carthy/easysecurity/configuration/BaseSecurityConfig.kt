package mj.carthy.easysecurity.configuration

import mj.carthy.easysecurity.manager.AuthenticationManager
import mj.carthy.easysecurity.repositories.SecurityContextRepository
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

open class BaseSecurityConfig {
  @Bean open fun securityWebFilterChain(
    authenticationManager: AuthenticationManager,
    securityContextRepository: SecurityContextRepository,
    http: ServerHttpSecurity
  ): SecurityWebFilterChain = http

    .csrf().disable()
    .formLogin().disable()
    .httpBasic().disable()

    .authenticationManager(authenticationManager)
    .securityContextRepository(securityContextRepository)

    .exceptionHandling()

    .authenticationEntryPoint { swe: ServerWebExchange, _: AuthenticationException ->
      Mono.fromRunnable { swe.response.statusCode = UNAUTHORIZED }
    }.accessDeniedHandler { swe: ServerWebExchange, _: AccessDeniedException ->
      Mono.fromRunnable { swe.response.statusCode = FORBIDDEN }
    }.and().build()
}