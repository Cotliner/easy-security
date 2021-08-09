package mj.carthy.easysecurity.tools

import com.google.common.annotations.VisibleForTesting
import mj.carthy.easyutils.helper.invoke
import org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE
import org.springframework.web.server.ServerWebExchange
import java.util.function.Function
import java.util.regex.Matcher
import java.util.regex.Pattern

const val BEARER = "Bearer"
const val DEFAULT_PATTERN: String = "^*([^ ]+)*\$"

private const val BEARER_PATTERN: String = "^$BEARER *([^ ]+)*$"
private const val NOT_MATCH: String = "The token is incorrect : %s"
private val GET_FIRST_GROUP = Function { matcher: Matcher -> matcher.group(INTEGER_ONE) }

fun challengePattern(pattern: String = BEARER_PATTERN): Pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE)

@VisibleForTesting fun ServerWebExchange.extract(header: String): String? = parse(this.request.headers.getFirst(header))

@VisibleForTesting fun parse(input: String?): String? {
  if (input == null) return null
  val matcher: Matcher = challengePattern().matcher(input)
  if (!matcher.matches()) throw IllegalArgumentException(NOT_MATCH(input))
  return GET_FIRST_GROUP.apply(matcher)
}