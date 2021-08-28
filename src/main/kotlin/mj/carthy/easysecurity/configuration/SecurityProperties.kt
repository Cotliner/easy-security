package mj.carthy.easysecurity.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.security.core.GrantedAuthority
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.MINUTES

@ConfigurationProperties(prefix = "security.token.access")
data class SecurityProperties(
  var signKey: String = "g~DS<EHd)Vr+C&#8:[ba",
  var amount: Long = 5,
  var unit: ChronoUnit = MINUTES,
  var authorizeRole: Set<GrantedAuthority> = emptySet()
)