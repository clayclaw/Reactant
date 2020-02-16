package dev.reactant.reactant.core.dependency.relation.interpreters

import dev.reactant.reactant.core.dependency.implied.ImpliedDepend
import dev.reactant.reactant.core.dependency.injection.InjectRequirement
import dev.reactant.reactant.core.dependency.injection.producer.ComponentProvider
import dev.reactant.reactant.core.dependency.injection.producer.Provider
import dev.reactant.reactant.core.dependency.relation.InterpretedProviderRelation
import dev.reactant.reactant.core.exception.ProviderRequirementCannotFulfilException
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

/**
 * Interpreter that can handle simple injection, argument injection, @ImpliedDepend and @ImpiledDependAll
 */
class ArgumentInjectionComponentProviderRelationInterpreter : SimpleInjectionComponentProviderRelationInterpreter() {
    override fun interpret(interpretTarget: Provider, providers: Set<Provider>): Set<InterpretedProviderRelation>? {
        if (interpretTarget !is ComponentProvider<*>) return null

        fun checkArgSizeAndType(type: KType, index: Int) {
            if (index >= type.arguments.size)
                throw IllegalArgumentException("Argument size of ${type.jvmErasure} is ${type.arguments.size}, but ImpliedDepend args included index $index")
            if (type.arguments[index].type == null)
                throw IllegalArgumentException("ImpliedDepend type ${type.jvmErasure} is required Generic args")
        }

        /**
         * Extract the relations from implied depends (if annotated with @ImpliedDepend)
         */
        fun extractImpliedDepends(type: KType, providers: Set<Provider>): Set<Provider> {
            return type.jvmErasure.java.let {
                if (it.isAnnotationPresent(ImpliedDepend::class.java)) {
                    val depend = it.getAnnotation(ImpliedDepend::class.java)

                    depend.argumentIndexes.union(depend.nullableArgumentIndexes.asIterable()).flatMap { argIndex ->
                        checkArgSizeAndType(type, argIndex)
                        val argumentType = type.arguments[argIndex].type!!
                        val nullable = depend.nullableArgumentIndexes.contains(argIndex)
                        providers.filter { provider -> provider.canProvideType(argumentType) }.let { impliedRequires ->
                            if (impliedRequires.isEmpty() && !nullable) {
                                // if a non-nullable impliedDepend have no provider found
                                throw ProviderRequirementCannotFulfilException(this, interpretTarget,
                                        "Target required $type, which implied that it require ${argumentType}," +
                                                " and it is not in the nullableArgumentIndexes, but there have no provider for it")
                                        .also { interpretTarget.catchedThrowable = it }
                            } else impliedRequires
                        }
                    }
                } else listOf()
            }.toSet()
        }


        return filterInterpretableRequirements(interpretTarget)
                .flatMap { requirement ->
                    // firstly, check whether the requirement is requiring a type which have @ImpliedDepend
                    // then extract the implied require relation if true, and union with the solved main requiring target
                    extractImpliedDepends(requirement.requiredType, providers).map {
                        InterpretedProviderRelation(
                                this, interpretTarget, it,
                                "It implied it is depend on the type"
                        )
                    }.union(
                            setOf(solve(interpretTarget, providers, requirement).let { (solution, priority) ->
                                InterpretedProviderRelation(
                                        this, interpretTarget, solution,
                                        "Solution that solve the injection from the providers list",
                                        setOf(requirement to solution),
                                        priority
                                )
                            })
                    )
                }.toSet()
    }


    override fun isRequirementInterpretable(requirement: InjectRequirement): Boolean = true
}
