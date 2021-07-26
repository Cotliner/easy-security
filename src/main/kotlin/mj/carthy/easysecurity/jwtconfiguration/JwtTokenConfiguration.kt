package mj.carthy.easysecurity.jwtconfiguration

import mj.carthy.easysecurity.service.JwtAuthenticateTokenService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate

@EnableConfigurationProperties(JwtSecurityProperties::class)
@Configuration class JwtTokenConfiguration(
  /* PROPERTIES */
  val jwtSecurityProperties: JwtSecurityProperties
) {
  @Bean fun jwtAuthenticateTokenService(mongoTemplate: ReactiveMongoTemplate): JwtAuthenticateTokenService = JwtAuthenticateTokenService(
    jwtSecurityProperties,
    mongoTemplate
  )
}