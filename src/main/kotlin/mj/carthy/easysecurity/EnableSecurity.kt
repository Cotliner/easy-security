package mj.carthy.easysecurity

import mj.carthy.easysecurity.jwtconfiguration.TokenConfiguration
import org.springframework.context.annotation.Import
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.ANNOTATION_CLASS
import kotlin.annotation.AnnotationTarget.CLASS

@Target(ANNOTATION_CLASS, CLASS)
@Retention(RUNTIME)
@Import(TokenConfiguration::class)
annotation class EnableSecurity