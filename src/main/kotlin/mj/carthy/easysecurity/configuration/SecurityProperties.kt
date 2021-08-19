package mj.carthy.easysecurity.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.MINUTES

@ConfigurationProperties(prefix = "security.jwt.access")
data class SecurityProperties(var signKey: String = "g~DS<EHd)Vr+C&#8:[ba", var amount: Long = 5, var unit: ChronoUnit = MINUTES)