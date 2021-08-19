package mj.carthy.easysecurity

import mj.carthy.easysecurity.configuration.RoboCopConfig
import org.springframework.context.annotation.Import

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Import(RoboCopConfig::class)
annotation class EnableRoboCop()