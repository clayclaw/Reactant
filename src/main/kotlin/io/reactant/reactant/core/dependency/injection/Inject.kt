package io.reactant.reactant.core.dependency.injection


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
annotation class Inject(val name: String = "")
