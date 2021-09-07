package mj.carthy.easysecurity.audit

import mj.carthy.easyutils.document.BaseDocument
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.mapping.event.ReactiveBeforeConvertCallback
import java.time.Instant
import java.util.*

@Configuration class AuditingConfiguration {
    @Bean fun documentUUIDAuditing(): ReactiveBeforeConvertCallback<BaseDocument<UUID?>> = AuditDocumentCallback(UUID::randomUUID)
    @Bean fun documentInstantAuditing(): ReactiveBeforeConvertCallback<BaseDocument<Instant?>> = AuditDocumentCallback(Instant::now)
}