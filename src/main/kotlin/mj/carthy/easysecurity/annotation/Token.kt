package mj.carthy.easysecurity.annotation

import mj.carthy.easysecurity.validator.TokenValidator
import javax.validation.Constraint
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [TokenValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Token(val message: String = "Invalid Token", val groups: Array<KClass<*>> = [], val payload: Array<KClass<*>> = [])
