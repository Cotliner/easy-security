package mj.carthy.easysecurity.configuration

import mj.carthy.easysecurity.service.RoboCopCleanService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@Configuration class RoboCopCleanConfig {
  /* PROPERTIES */
  @Bean fun roboCopCron(@Value("\${security.robo-cop.cron:#{null}}") cron: String?): String = cron ?: "0 0 0 * * ?"
  /* SERVICES */
  @Bean fun roboCopService(mongoTemplate: ReactiveMongoTemplate) = RoboCopCleanService(mongoTemplate)
}