package net.swamphut.swampium.core.dependency.injectable.producer

import net.swamphut.swampium.core.dependency.injection.Inject
import net.swamphut.swampium.core.dependency.injection.InjectRequirement
import net.swamphut.swampium.core.dependency.provide.Provide
import net.swamphut.swampium.core.swobject.instance.SwObjectInstanceManager
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberProperties

/**
 * ProducerWrapper of SwObject class constructor
 */
class SwObjectInjectableProducerWrapper<T : Any>(
        val swObjectClass: KClass<T>,
        override val namePattern: String,
        private val swObjectInstanceManager: SwObjectInstanceManager
) : InjectableProducerWrapper {

    override val producer: (KType, String, Any) -> Any = { _: KType, _: String, _: Any ->
        swObjectInstanceManager.getInstance(swObjectClass) ?: constructSwObjectInstance()
    }

    val resolvedRequirements = HashMap<InjectRequirement, Any>()


    /**
     * The required injectable of the constructor parameters
     */
    private val constructorInjectRequirements = swObjectClass.constructors.first().parameters
            .map(InjectRequirement.Companion::fromParameter)

    /**
     * The required injectable of the properties
     */
    @Suppress("UNCHECKED_CAST")
    private val propertiesInjectRequirements = swObjectClass.memberProperties
            .filter { it.annotations.any { annotation -> annotation is Inject } && it is KMutableProperty1<T, *> }
            .map { it as KMutableProperty1<T, Any> }
            .map { it to InjectRequirement.fromProperty(it) }.toMap()

    /**
     * All required injectable
     */
    val requiredInjectable get() = constructorInjectRequirements.union(propertiesInjectRequirements.values)


    override val productType: KType = swObjectClass::class.createType()


    /**
     * Construct the SwObject instance and put into instance manager
     */
    private fun constructSwObjectInstance(): T {
        // Check fulfilled
        if (notFulfilledRequirements.isNotEmpty())
            throw IllegalStateException("SwObject inject requirements not fulfilled")

        val instance = swObjectClass.constructors.first()
                .call(*constructorInjectRequirements.map { resolvedRequirements[it] }.toTypedArray())

        propertiesInjectRequirements.forEach { it.key.set(instance, resolvedRequirements[it.value]!!) }
        swObjectInstanceManager.putInstance(instance)
        return instance
    }

    /**
     * Filter out which injectable requirements is not yet fulfilled
     */
    val notFulfilledRequirements
        get() = constructorInjectRequirements
                .union(propertiesInjectRequirements.values)
                .filter { !resolvedRequirements.containsKey(it) }

    val fulfilled get() = notFulfilledRequirements.size < 0

    companion object {
        fun <T : Any> fromSwObjectClass(swObjectClass: KClass<T>, instanceManager: SwObjectInstanceManager) =
                SwObjectInjectableProducerWrapper(swObjectClass,
                        Provide.fromElement(swObjectClass).namePattern, instanceManager)
    }
}
