package mj.carthy.easysecurity.document

import mj.carthy.easyutils.document.BaseDocument
import java.time.Instant
import java.util.*

data class RoboCop(val mappedId: UUID, val deadline: Instant): BaseDocument<UUID>()