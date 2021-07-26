package mj.carthy.easysecurity.jwtconfiguration

import com.fasterxml.jackson.databind.ObjectMapper
import mj.carthy.easysecurity.service.KafkaConsumerService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate

@Configuration class KafkaSecurityConfiguration {
  @Bean fun kafkaConsumerService(objectMapper: ObjectMapper, mongoTemplate: ReactiveMongoTemplate) = KafkaConsumerService(objectMapper, mongoTemplate)
}