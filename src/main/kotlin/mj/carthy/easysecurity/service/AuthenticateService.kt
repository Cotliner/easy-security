package mj.carthy.easysecurity.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import kotlinx.coroutines.reactive.awaitFirstOrNull
import mj.carthy.easysecurity.document.RoboCop
import mj.carthy.easysecurity.helper.toUserAuth
import mj.carthy.easysecurity.configuration.SecurityProperties
import mj.carthy.easysecurity.model.UserAuth
import mj.carthy.easyutils.helper.uuid
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import java.time.LocalDate
import java.time.Period
import java.util.*

class AuthenticateService(
  /* PROPERTIES */
  private val securityProperties: SecurityProperties,
  /* REPOSITORIES */
  private val mongoTemplate: ReactiveMongoTemplate
) {
  companion object {
    /* QUERIES PARAM */
    const val MAPPED_ID_PARAM: String = "mappedId"
  }

  suspend fun tokenToUserAuth(claims: Claims, sessionId: UUID): UserAuth? {
    val query = Query().addCriteria(Criteria.where(MAPPED_ID_PARAM).`is`(sessionId))

    if (mongoTemplate.find(query, RoboCop::class.java).awaitFirstOrNull() != null) return null

    return claims.toUserAuth()
  }

  fun tokenParser(token: String, signKey: String = securityProperties.signKey): Claims = Jwts.parser().setSigningKey(signKey).parseClaimsJws(token).body
  fun sessionId(claims: Claims): UUID = claims.subject.uuid
}