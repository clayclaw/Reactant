package dev.reactant.reactant.core.dependency.implied

/**
 * Declare that who injected the target class is implied requiring the generic type injectable
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ImpliedDepend(
        /**
         * Indexes of generic type arguments which is a implied depend type, start from 0
         * All of these type will marks as required by the component
         * There must have at least 1 available provider, otherwise exception will be throw
         */
        val argumentIndexes: IntArray = [],
        /**
         * All of these type will marks as required by the component
         * It will not throw exception if there have none of these provider
         */
        val nullableArgumentIndexes: IntArray = []
)
