package mj.carthy.easysecurity.service

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingle
import mj.carthy.easysecurity.document.RoboCop
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.scheduling.annotation.Scheduled
import java.time.Instant.now

class RoboCopService(
  /* CLIENTS */
  private val objectMapper: ObjectMapper,
  /* REPOSITORIES */
  private val mongoTemplate: ReactiveMongoTemplate,
) {
  companion object {
    /* QUERIES PARAM */
    const val DEADLINE: String = "deadline"
  }

  @DelicateCoroutinesApi
  @KafkaListener(topics = ["#{@roboCopTopic}"], groupId = "#{@roboCopGroupId}")
  fun consume(session: String) { GlobalScope.launch { mongoTemplate.save(objectMapper.readValue(session, RoboCop::class.java)).awaitSingle() } }

  @DelicateCoroutinesApi
  @Scheduled(cron = "#{@roboCopCron}") fun deleteExpireRoboCop() { GlobalScope.launch { mongoTemplate.remove(Query(Criteria.where(DEADLINE).lt(now())), RoboCop::class.java).awaitSingle() } }
}