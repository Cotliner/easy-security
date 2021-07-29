package mj.carthy.easysecurity.helper

import mj.carthy.easyutils.enums.Sex

fun Sex.inversed(): Sex = when(this) {
  Sex.MALE -> Sex.FEMALE
  Sex.FEMALE -> Sex.MALE
}