package mj.carthy.easysecurity.service

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.reactor.awaitSingle
import mj.carthy.easysecurity.document.RoboCop
import mj.carthy.easyutils.helper.consume
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import org.springframework.scheduling.annotation.Scheduled
import java.time.Instant.now

class RoboCopCleanService(
  /* REPOSITORIES */
  private val mongoTemplate: ReactiveMongoTemplate,
) {

  companion object {
    /* QUERIES PARAM */
    const val DEADLINE: String = "deadline"
  }

  @DelicateCoroutinesApi
  @Scheduled(cron = "#{@roboCopCron}")
  fun deleteExpireRoboCop() { consume { mongoTemplate.remove(Query(where(DEADLINE).lt(now())), RoboCop::class.java).awaitSingle() } }
}