package mj.carthy.easysecurity.document

import mj.carthy.easyutils.document.BaseDocument
import java.util.*

data class Exclude(val mappedId: UUID): BaseDocument<UUID>()