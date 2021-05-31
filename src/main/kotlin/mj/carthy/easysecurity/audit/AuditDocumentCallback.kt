package mj.carthy.easysecurity.audit

import com.google.common.annotations.VisibleForTesting
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.mono
import mj.carthy.easysecurity.authentication.UserTokenAuthentication
import mj.carthy.easyutils.document.BaseDocument
import org.reactivestreams.Publisher
import org.springframework.data.mongodb.core.mapping.event.ReactiveBeforeConvertCallback
import org.springframework.security.core.context.ReactiveSecurityContextHolder
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

    if (document.id != null) setCreated(document, username, now)

    setLastModifiedDate(document, username, now)

    document
  }

  @VisibleForTesting fun <T : BaseDocument<UUID?>> setCreated(
    documentToAudit: T,
    username: String,
    date: Instant
  ) {
    documentToAudit.id = UUID.randomUUID()
    documentToAudit.createdBy = username
    documentToAudit.createdDate = date
  }

  @VisibleForTesting fun <T : BaseDocument<UUID?>> setLastModifiedDate(
    documentToAudit: T,
    username: String,
    date: Instant
  ) {
    documentToAudit.lastModifiedBy = username
    documentToAudit.lastModifiedDate = date
  }

  @VisibleForTesting suspend fun username(): String = ReactiveSecurityContextHolder.getContext().map {
    getCurrentAuditor(it)
  }.switchIfEmpty(Mono.just(SYSTEM)).awaitSingle()

  @VisibleForTesting fun getCurrentAuditor(auth: SecurityContext): String = when (auth.authentication) {
    is UserTokenAuthentication -> when (auth.authentication.principal) {
      is UserDetails -> (auth.authentication.principal as UserDetails).username
      is String -> auth.authentication.principal as String
      else -> SYSTEM
    }
    else -> SYSTEM
  }
}