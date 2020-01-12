package dev.reactant.reactant.core.dependency.relation

import dev.reactant.reactant.core.dependency.implied.ImpliedDepend
import dev.reactant.reactant.core.dependency.injection.InjectRequirement
import dev.reactant.reactant.core.dependency.injection.producer.ComponentProvider
import dev.reactant.reactant.core.dependency.injection.producer.Provider
import dev.reactant.reactant.core.exception.ProviderRequirementCannotFulfilException
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

/**
 * Interpreter that handle arugment injection, @ImpliedDepend and @ImpiledDependAll
 */
class ArgumentInjectionComponentProviderRelationInterpreter : SimpleInjectionComponentProviderRelationInterpreter() {
    override fun interpret(interpretTarget: Provider, providers: Set<Provider>): Set<InterpretedProviderRelation>? {
        if (interpretTarget !is ComponentProvider<*>) return null

        fun checkArgSizeAndType(type: KType, index: Int) {
            if (index >= type.arguments.size)
                throw IllegalArgumentException("Argument size of ${type.jvmErasure} is ${type.arguments.size}, but ImpliedDepend args included index $index");
            if (type.arguments[index].type == null)
                throw IllegalArgumentException("ImpliedDepend type ${type.jvmErasure} is required Generic args");
        }

        fun walkType(type: KType, providers: Set<Provider>): HashSet<Provider> {
            val result = HashSet<Provider>();
            type.jvmErasure.java.let {
                if (it.isAnnotationPresent(ImpliedDepend::class.java)) {
                    val depend = it.getAnnotation(ImpliedDepend::class.java)

                    depend.argumentIndexes.union(depend.nullableArgumentIndexes.asIterable()).forEach { argIndex ->
                        checkArgSizeAndType(type, argIndex)
                        val argumentType = type.arguments[argIndex].type!!
                        providers.filter { it.canProvideType(argumentType) }
                                .let {
                                    if (it.isEmpty() && !depend.nullableArgumentIndexes.contains(argIndex)) {
                                        throw ProviderRequirementCannotFulfilException(this, interpretTarget,
                                                "Target required $type, which implied that it require ${argumentType}," +
                                                        " and it is not in the nullableArgumentIndexes, but there have no provider for it")
                                                .also { interpretTarget.catchedThrowable = it }
                                    } else result.addAll(it)
                                }
                    }
                }
            }
            return result;
        }


        return filterInterpretableRequirements(interpretTarget)
                .flatMap { requirement ->
                    walkType(requirement.requiredType, providers).map {
                        InterpretedProviderRelation(
                                this, interpretTarget, it,
                                "It implied it is depend on the type"
                        )
                    }.union(
                            setOf(solve(interpretTarget, providers, requirement).let { (solution, priority) ->
                                InterpretedProviderRelation(
                                        this, interpretTarget, solution,
                                        "Solution that solve the argumented injection from the providers list",
                                        setOf(requirement to solution),
                                        priority
                                )
                            })
                    )
                }.toSet()
    }


    override fun isRequirementInterpretable(requirement: InjectRequirement): Boolean = requirement.requiredType.run { arguments.isNotEmpty() }
}
