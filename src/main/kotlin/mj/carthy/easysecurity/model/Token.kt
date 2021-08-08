package mj.carthy.easysecurity.model

import java.time.Instant

data class Token(val value: String, val limit: Instant)