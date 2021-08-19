package mj.carthy.easysecurity

import mj.carthy.easysecurity.configuration.DefaultSecurityConfig
import mj.carthy.easysecurity.configuration.SecurityConfig
import mj.carthy.easysecurity.configuration.TokenConfig
import org.springframework.context.annotation.Import
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.ANNOTATION_CLASS
import kotlin.annotation.AnnotationTarget.CLASS

@Target(ANNOTATION_CLASS, CLASS)
@Retention(RUNTIME)
@Import(SecurityConfig::class, TokenConfig::class, DefaultSecurityConfig::class)
annotation class EnableDefaultSecurity