package mj.carthy.easysecurity.jwtconfiguration

import mj.carthy.easysecurity.service.AuthenticateService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate

@EnableConfigurationProperties(SecurityProperties::class)
@Configuration class TokenConfiguration(
  /* PROPERTIES */
  val securityProperties: SecurityProperties
) {
  @Bean fun authenticateService(mongoTemplate: ReactiveMongoTemplate): AuthenticateService = AuthenticateService(securityProperties, mongoTemplate)
}