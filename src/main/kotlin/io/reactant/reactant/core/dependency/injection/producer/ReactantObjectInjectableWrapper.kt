package io.reactant.reactant.core.dependency.injection.producer

import io.reactant.reactant.core.ReactantCore
import io.reactant.reactant.core.dependency.injection.Inject
import io.reactant.reactant.core.dependency.injection.InjectRequirement
import io.reactant.reactant.core.exception.InjectRequirementNotFulfilledException
import io.reactant.reactant.core.exception.RequiredInjectableCannotBeActiveException
import io.reactant.reactant.core.reactantobj.container.Reactant
import io.reactant.reactant.core.reactantobj.instance.ReactantObjectInstanceManager
import io.reactant.reactant.utils.reflections.FieldsFinder
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

/**
 * ProducerWrapper of ReactantObject class constructor
 */
class ReactantObjectInjectableWrapper<T : Any>(
        val reactantObjectClass: KClass<T>,
        override val namePattern: String,
        private val reactantObjectInstanceManager: ReactantObjectInstanceManager
) : InjectableWrapper {
    var catchedThrowable: Throwable? = null
    override val disabledReason: Throwable?
        get() = when {
            !fulfilled -> InjectRequirementNotFulfilledException(this, notFulfilledRequirements.toSet())
            else -> catchedThrowable
        }

    override val producer: (requestedType: KType, requestedName: String, requester: InjectableWrapper) -> Any = { _, _, _ -> getInstance() }

    fun getInstance() = reactantObjectInstanceManager.getInstance(reactantObjectClass)
            ?: throw IllegalStateException("${reactantObjectClass.qualifiedName} not yet initialized")

    fun isInitialized() = reactantObjectInstanceManager.getInstance(reactantObjectClass) != null

    val resolvedRequirements = HashMap<InjectRequirement, InjectableWrapper>()


    /**
     * The required injectable of the constructor parameters
     */
    private val constructorInjectRequirements = reactantObjectClass.constructors.first().parameters
            .map(InjectRequirement.Companion::fromParameter)

    /**
     * The required injectable of the properties
     */
    @Suppress("UNCHECKED_CAST")
    private val propertiesInjectRequirements = FieldsFinder.getAllDeclaredPropertyRecursively(reactantObjectClass)
            .asSequence()
            // filter out constructor declared properties
            .filter { property -> !reactantObjectClass.constructors.first().parameters.any { it.name == property.name } }
            .filter { it.javaField?.isAnnotationPresent(Inject::class.java) ?: false }
            .onEach { if (it !is KMutableProperty<*>) ReactantCore.logger.error("$productType::${it.name} is annotated with @Inject but is not mutable") }
            .mapNotNull { it as? KMutableProperty1<T, Any> }
            .map { it to InjectRequirement.fromProperty(it) }
            .toList().toMap()

    /**
     * All required injectable
     */
    val injectRequirements get() = constructorInjectRequirements.union(propertiesInjectRequirements.values)


    override val productType: KType
        get() {
            return reactantObjectClass.createType()
        }

    /**
     * Construct the ReactantObject instance
     * @throws RequiredInjectableCannotBeActiveException
     */
    fun constructReactantObjectInstance(): T {
        // Check fulfilled
        if (notFulfilledRequirements.isNotEmpty())
            throw IllegalStateException("ReactantObject inject requirements not fulfilled")

        val requiredInjectables = resolvedRequirements.runCatching {
            map { it.key to it.value.producer(it.key.requiredType, it.key.name, this@ReactantObjectInjectableWrapper) }
                    .toMap()
        }.getOrElse { throw RequiredInjectableCannotBeActiveException(this, it).also { e -> catchedThrowable = e } }


        val instance: T = reactantObjectClass.constructors.first().runCatching {
            isAccessible = true
            call(*constructorInjectRequirements.map { requiredInjectables[it] }.toTypedArray())
        }.getOrElse { throw RequiredInjectableCannotBeActiveException(this, it).also { e -> catchedThrowable = e } }

        propertiesInjectRequirements.forEach {
            it.key.isAccessible = true;
            it.key.set(instance, requiredInjectables[it.value] ?: error(""))
        }

        reactantObjectInstanceManager.putInstance(instance)
        return instance
    }

    /**
     * Filter out which injectable requirements is not yet fulfilled
     */
    val notFulfilledRequirements
        get() = injectRequirements
                .filter { !resolvedRequirements.containsKey(it) }

    val fulfilled get() = notFulfilledRequirements.isEmpty()

    companion object {
        fun <T : Any> fromReactantObjectClass(reactantObjectClass: KClass<T>, instanceManager: ReactantObjectInstanceManager) =
                ReactantObjectInjectableWrapper(reactantObjectClass,
                        Reactant.fromElement(reactantObjectClass).name, instanceManager)
    }
}
