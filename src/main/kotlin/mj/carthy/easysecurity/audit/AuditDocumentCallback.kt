package mj.carthy.easysecurity.audit

import com.google.common.annotations.VisibleForTesting
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import mj.carthy.easysecurity.authentication.UserTokenAuth
import mj.carthy.easyutils.document.BaseDocument
import org.reactivestreams.Publisher
import org.springframework.data.mongodb.core.mapping.event.ReactiveBeforeConvertCallback
import org.springframework.security.core.context.ReactiveSecurityContextHolder.getContext
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.userdetails.UserDetails
import java.time.Instant

class AuditDocumentCallback<ID>(private val idGenerator: () -> ID): ReactiveBeforeConvertCallback<BaseDocument<ID?>> {

  companion object { const val SYSTEM = "system" }

  override fun onBeforeConvert(
    document: BaseDocument<ID?>,
    collection: String
  ): Publisher<BaseDocument<ID?>> = mono {
    val now = Instant.now()
    val username = username()

    if (document.id == null) setCreated(document, username, now)

    setLastModifiedDate(document, username, now)

    document
  }

  @VisibleForTesting fun <T : BaseDocument<ID?>> setCreated(
    documentToAudit: T,
    username: String,
    date: Instant
  ): Unit = with(documentToAudit) {
    id = idGenerator()
    createdBy = username
    createdDate = date
  }

  @VisibleForTesting fun <T : BaseDocument<ID?>> setLastModifiedDate(
    documentToAudit: T,
    username: String,
    date: Instant
  ): Unit = with(documentToAudit) {
    lastModifiedBy = username
    lastModifiedDate = date
  }

  @VisibleForTesting suspend fun username(): String = getContext().map { it.currentAuditor }.awaitSingleOrNull() ?: SYSTEM

  @VisibleForTesting val SecurityContext.currentAuditor get(): String = when (authentication) {
    is UserTokenAuth -> when (authentication.principal) {
      is UserDetails -> (authentication.principal as UserDetails).username
      is String -> authentication.principal as String
      else -> SYSTEM
    }
    else -> SYSTEM
  }
}