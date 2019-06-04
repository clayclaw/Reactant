package net.swamphut.swampium.core.dependency.injectable.producer

import net.swamphut.swampium.core.dependency.injection.Inject
import net.swamphut.swampium.core.dependency.injection.InjectRequirement
import net.swamphut.swampium.core.swobject.instance.SwObjectInstanceManager
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberProperties

/**
 * The producer info for resolve loading order
 */
class SwObjectInjectableProducerWrapper<T : Any>(
        val swObjectClass: KClass<T>,
        private val swObjectInstanceManager: SwObjectInstanceManager
) : InjectableProducerWrapper {
    override val producer = {
        swObjectInstanceManager.getInstance(swObjectClass) ?: constructSwObjectInstance()
    }

    val resolvedRequirements = HashMap<InjectRequirement, Any>()

    private val constructorInjectRequirements = swObjectClass.constructors.first().parameters
            .map(InjectRequirement.Companion::fromParameter)

    @Suppress("UNCHECKED_CAST")
    private val propertiesInjectRequirements = swObjectClass.memberProperties
            .filter { it.annotations.any { annotation -> annotation is Inject } && it is KMutableProperty1<T, *> }
            .map { it as KMutableProperty1<T, Any> }
            .map { it to InjectRequirement.fromProperty(it) }.toMap()

    val requiredInjectable: Set<InjectRequirement>
        get() = constructorInjectRequirements.union(propertiesInjectRequirements.values)

    override val productType: KType = swObjectClass::class.createType()


    private fun constructSwObjectInstance(): T {
        // Check fulfilled
        if (getNotFulfilledRequirements().isNotEmpty())
            throw IllegalStateException("SwObject inject requirements not fulfilled")

        val instance = swObjectClass.constructors.first()
                .call(*constructorInjectRequirements.map { resolvedRequirements[it] }.toTypedArray())

        propertiesInjectRequirements.forEach { it.key.set(instance, resolvedRequirements[it.value]!!) }
        swObjectInstanceManager.putInstance(instance)
        return instance
    }

    private fun getNotFulfilledRequirements() =
            constructorInjectRequirements
                    .union(propertiesInjectRequirements.values)
                    .filter { !resolvedRequirements.containsKey(it) }

}
