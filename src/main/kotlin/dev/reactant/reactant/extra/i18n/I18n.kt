package dev.reactant.reactant.extra.i18n

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class I18n(
        val name: String,
        val description: String = ""
)
