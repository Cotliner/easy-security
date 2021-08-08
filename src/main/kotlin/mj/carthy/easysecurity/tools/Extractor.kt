package mj.carthy.easysecurity.tools

import com.google.common.annotations.VisibleForTesting
import org.apache.commons.lang3.math.NumberUtils
import org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE
import org.springframework.web.server.ServerWebExchange
import java.util.function.Function
import java.util.regex.Matcher
import java.util.regex.Pattern

const val BEARER = "Bearer"

private const val PATTERN = "^$BEARER *([^ ]+)*$"

private val CHALLENGE_PATTERN = Pattern.compile(PATTERN, Pattern.CASE_INSENSITIVE)

private const val NOT_MATCH = "The token is incorrect : %s"

private val GET_FIRST_GROUP = Function { matcher: Matcher -> matcher.group(INTEGER_ONE) }

@VisibleForTesting fun ServerWebExchange.extract(header: String): String? = parse(this.request.headers.getFirst(header))

@VisibleForTesting fun parse(input: String?): String? {
  if (input == null) return null
  val matcher: Matcher = CHALLENGE_PATTERN.matcher(input)
  if (!matcher.matches()) throw IllegalArgumentException(String.format(NOT_MATCH, input))
  return GET_FIRST_GROUP.apply(matcher)
}