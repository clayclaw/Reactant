package dev.reactant.reactant.core.dependency.implied

/**
 * Declare that who injected the target class is implied requiring the generic type injectable
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ImpliedDepend(
        /**
         * Indexes of generic type arguments which is a implied depend type, start from 0
         */
        val typeArgumentIndexes: IntArray,
        /**
         * Convert the type to nullable
         */
        val nullableArgumentIndexes: IntArray = []
)
