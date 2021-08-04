package mj.carthy.easysecurity

import mj.carthy.easysecurity.jwtconfiguration.RoboCopConfiguration
import org.springframework.context.annotation.Import

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Import(RoboCopConfiguration::class)
annotation class EnableRoboCop()