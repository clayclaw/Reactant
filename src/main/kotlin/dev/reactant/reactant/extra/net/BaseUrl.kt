package dev.reactant.reactant.extra.net

/**
 * Use to specify the base url of the api service interface
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class BaseUrl(val baseUrl: String)
