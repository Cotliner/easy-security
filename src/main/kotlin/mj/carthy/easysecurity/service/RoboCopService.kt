package mj.carthy.easysecurity.service

import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.reactor.awaitSingle
import mj.carthy.easysecurity.document.RoboCop
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.scheduling.annotation.Scheduled
import java.time.Instant.now

class RoboCopService(
  private val gson: Gson,
  /* REPOSITORIES */
  private val mongoTemplate: ReactiveMongoTemplate,
) {
  companion object {
    /* QUERIES PARAM */
    const val MAPPED_ID: String = "mappedId"
    const val DEADLINE: String = "deadline"
  }

  @DelicateCoroutinesApi
  @KafkaListener(topics = ["#{@roboCopTopic}"], groupId = "#{@roboCopGroupId}")
  fun consume(record: ConsumerRecord<String, Map<String, String>>) = with(gson.make<RoboCop>(record)) { GlobalScope.launch { if (!isExist()) save() } }

  private suspend fun RoboCop.isExist(): Boolean = mongoTemplate.exists(Query(where(MAPPED_ID).`is`(this.id)), RoboCop::class.java).awaitSingle()
  private suspend fun RoboCop.save(): RoboCop = mongoTemplate.save(this).awaitSingle()

  private inline fun <reified T> Gson.make(record: ConsumerRecord<String, Map<String, String>>) = fromJson(toJsonTree(record.value()), T::class.java)

  @DelicateCoroutinesApi
  @Scheduled(cron = "#{@roboCopCron}")
  fun deleteExpireRoboCop() { GlobalScope.launch { mongoTemplate.remove(Query(where(DEADLINE).lt(now())), RoboCop::class.java).awaitSingle() } }
}