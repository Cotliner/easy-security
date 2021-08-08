package mj.carthy.easysecurity.jwtconfiguration

import org.apache.commons.lang3.math.NumberUtils.LONG_ONE
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.MINUTES

@ConfigurationProperties(prefix = "security.jwt")
data class SecurityProperties(val signKey: String = "g~DS<EHd)Vr+C&#8:[ba", val amount: Long = 5, val unit: ChronoUnit = MINUTES)