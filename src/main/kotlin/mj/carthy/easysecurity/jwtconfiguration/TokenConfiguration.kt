package mj.carthy.easysecurity.jwtconfiguration

import mj.carthy.easysecurity.manager.AuthenticationManager
import mj.carthy.easysecurity.repositories.SecurityContextRepository
import mj.carthy.easysecurity.service.AuthenticateService
import mj.carthy.easysecurity.validator.TokenValidator
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate

@EnableConfigurationProperties(SecurityProperties::class)
@Configuration class TokenConfiguration(
  /* PROPERTIES */
  val securityProperties: SecurityProperties
) {
  @Bean fun tokenValidator(): TokenValidator = TokenValidator()

  @Bean fun authenticateService(mongoTemplate: ReactiveMongoTemplate): AuthenticateService = AuthenticateService(securityProperties, mongoTemplate)

  @Bean fun authenticationManager(authenticateService: AuthenticateService) = AuthenticationManager(authenticateService)

  @Bean fun securityContextRepository(authenticationManager: AuthenticationManager) = SecurityContextRepository(authenticationManager)
}