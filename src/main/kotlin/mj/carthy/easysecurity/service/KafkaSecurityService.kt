package mj.carthy.easysecurity.service

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingle
import mj.carthy.easysecurity.document.Exclude
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.kafka.annotation.KafkaListener

class KafkaSecurityService(
  /* CLIENTS */
  private val objectMapper: ObjectMapper,
  /* REPOSITORIES */
  private val mongoTemplate: ReactiveMongoTemplate,
) {
  @DelicateCoroutinesApi
  @KafkaListener(topics = ["#{@kafkaSecurityTopic}"], groupId = "#{@kafkaSecurityGroupId}")
  fun consume(session: String) { GlobalScope.launch { mongoTemplate.save(objectMapper.readValue(session, Exclude::class.java)).awaitSingle() } }
}