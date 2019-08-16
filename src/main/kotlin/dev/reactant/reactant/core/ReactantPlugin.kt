package dev.reactant.reactant.core

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class ReactantPlugin(val servicePackages: Array<String>)
