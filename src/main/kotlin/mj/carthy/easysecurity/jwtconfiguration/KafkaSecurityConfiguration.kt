package mj.carthy.easysecurity.jwtconfiguration

import com.fasterxml.jackson.databind.ObjectMapper
import mj.carthy.easysecurity.service.KafkaSecurityService
import mj.carthy.easyutils.helper.string
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import java.util.*
import java.util.UUID.randomUUID

@Configuration class KafkaSecurityConfiguration {
  /* PROPERTIES */
  @Bean fun kafkaSecurityTopic(@Value("\${security.kafka.topic:#{null}}") topic: String?): String = topic ?: "session"
  @Bean fun kafkaSecurityGroupId(@Value("\${security.kafka.group-id:#{null}}") groupId: String?): String = groupId ?: randomUUID().string
  /* SERVICES */
  @Bean fun kafkaSecurityService(objectMapper: ObjectMapper, mongoTemplate: ReactiveMongoTemplate) = KafkaSecurityService(objectMapper, mongoTemplate)
}