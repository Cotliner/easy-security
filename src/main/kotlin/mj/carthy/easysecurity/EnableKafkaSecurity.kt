package mj.carthy.easysecurity

import mj.carthy.easysecurity.jwtconfiguration.KafkaSecurityConfiguration
import org.springframework.context.annotation.Import

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Import(KafkaSecurityConfiguration::class)
annotation class EnableKafkaSecurity()