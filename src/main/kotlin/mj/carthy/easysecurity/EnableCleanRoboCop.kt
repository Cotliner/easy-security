package mj.carthy.easysecurity

import mj.carthy.easysecurity.configuration.RoboCopCleanConfig
import org.springframework.context.annotation.Import

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Import(RoboCopCleanConfig::class)
annotation class EnableCleanRoboCop
