package mj.carthy.easysecurity.configuration

import com.google.gson.Gson
import mj.carthy.easysecurity.service.RoboCopService
import mj.carthy.easyutils.helper.string
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.scheduling.annotation.EnableScheduling
import java.util.UUID.randomUUID

@EnableScheduling
@Configuration class RoboCopConfig {
  /* PROPERTIES */
  @Bean fun roboCopTopic(@Value("\${security.robo-cop.topic:#{null}}") topic: String?): String = topic ?: "robo-cop"
  @Bean fun roboCopGroupId(@Value("\${security.robo-cop.group-id:#{null}}") groupId: String?): String = groupId ?: randomUUID().string
  @Bean fun roboCopCron(@Value("\${security.robo-cop.cron:#{null}}") cron: String?): String = cron ?: "0 0 0 * * ?"
  /* SERVICES */
  @Bean fun roboCopService(gson: Gson, mongoTemplate: ReactiveMongoTemplate) = RoboCopService(gson, mongoTemplate)
}