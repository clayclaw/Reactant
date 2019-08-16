package dev.reactant.reactant.core.dependency.implied

/**
 * Declare that who injected the target class is implied requiring the generic type injectable
 * All possible providers will be required (not only one)
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ImpliedDependAll(
        /**
         * Indexes of generic type arguments which is a implied depend type, start from 0
         */
        val typeArgumentIndexes: IntArray
)
