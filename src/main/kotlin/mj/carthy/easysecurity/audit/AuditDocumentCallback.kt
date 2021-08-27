package mj.carthy.easysecurity.audit

import com.google.common.annotations.VisibleForTesting
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.mono
import mj.carthy.easysecurity.authentication.UserTokenAuth
import mj.carthy.easyutils.document.BaseDocument
import org.reactivestreams.Publisher
import org.springframework.data.mongodb.core.mapping.event.ReactiveBeforeConvertCallback
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.ReactiveSecurityContextHolder.getContext
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.userdetails.UserDetails
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.*

class AuditDocumentCallback : ReactiveBeforeConvertCallback<BaseDocument<UUID?>> {

  companion object { const val SYSTEM = "system" }

  override fun onBeforeConvert(
    document: BaseDocument<UUID?>,
    collection: String
  ): Publisher<BaseDocument<UUID?>> = mono {
    val now = Instant.now()
    val username = username()

    if (document.id == null) setCreated(document, username, now)

    setLastModifiedDate(document, username, now)

    document
  }

  @VisibleForTesting fun <T : BaseDocument<UUID?>> setCreated(
    documentToAudit: T,
    username: String,
    date: Instant
  ): Unit = with(documentToAudit) {
    this.id = UUID.randomUUID()
    this.createdBy = username
    this.createdDate = date
  }

  @VisibleForTesting fun <T : BaseDocument<UUID?>> setLastModifiedDate(
    documentToAudit: T,
    username: String,
    date: Instant
  ): Unit = with(documentToAudit) {
    this.lastModifiedBy = username
    this.lastModifiedDate = date
  }

  @VisibleForTesting suspend fun username(): String = getContext().map { it.getCurrentAuditor() }.awaitSingle() ?: SYSTEM

  @VisibleForTesting fun SecurityContext.getCurrentAuditor(): String = when (this.authentication) {
    is UserTokenAuth -> when (this.authentication.principal) {
      is UserDetails -> (this.authentication.principal as UserDetails).username
      is String -> this.authentication.principal as String
      else -> SYSTEM
    }
    else -> SYSTEM
  }
}