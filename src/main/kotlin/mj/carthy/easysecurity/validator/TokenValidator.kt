package mj.carthy.easysecurity.validator

import mj.carthy.easysecurity.annotation.Token
import mj.carthy.easysecurity.tools.DEFAULT_PATTERN
import mj.carthy.easysecurity.tools.challengePattern
import org.apache.commons.lang3.StringUtils.isBlank
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class TokenValidator: ConstraintValidator<Token, String> {

  companion object {
    private const val TOKEN_BLANK_ERROR_MESSAGE = "Token value can not be null"
    private const val TOKEN_FORMAT_UNMATCHED = "Token format unmatched"
  }

  override fun isValid(value: String, context: ConstraintValidatorContext): Boolean = if (isBlank(value)) {
    context.buildConstraintViolationWithTemplate(TOKEN_BLANK_ERROR_MESSAGE).addConstraintViolation().disableDefaultConstraintViolation()
    FALSE
  } else if (!challengePattern(DEFAULT_PATTERN).matcher(value).matches()) {
    context.buildConstraintViolationWithTemplate(TOKEN_FORMAT_UNMATCHED).addConstraintViolation().disableDefaultConstraintViolation()
    FALSE
  } else TRUE
}