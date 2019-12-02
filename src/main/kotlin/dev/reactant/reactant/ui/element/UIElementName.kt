package dev.reactant.reactant.ui.element

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class UIElementName(
        val name: String,
        val namespace: String = "ui"
)
