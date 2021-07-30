package mj.carthy.easysecurity.helper

import mj.carthy.easysecurity.enums.Sex
import mj.carthy.easyutils.exception.UnprocessedException
import mj.carthy.easyutils.helper.Errors.Companion.PROPERTY_NOT_FOUND

const val UNKNOWN_SEX = "Can not inverse unknown sex"

fun Sex.inversed(): Sex = when(this) {
  Sex.MALE -> Sex.FEMALE
  Sex.FEMALE -> Sex.MALE
  Sex.UNKNOWN -> throw UnprocessedException(PROPERTY_NOT_FOUND, UNKNOWN_SEX)
}