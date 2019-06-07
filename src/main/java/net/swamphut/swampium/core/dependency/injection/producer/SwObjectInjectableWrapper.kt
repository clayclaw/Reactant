package net.swamphut.swampium.core.dependency.injection.producer

import net.swamphut.swampium.core.Swampium
import net.swamphut.swampium.core.dependency.injection.Inject
import net.swamphut.swampium.core.dependency.injection.InjectRequirement
import net.swamphut.swampium.core.exception.InjectRequirementNotFulfilledException
import net.swamphut.swampium.core.exception.RequiredInjectableCannotBeActiveException
import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.instance.SwObjectInstanceManager
import net.swamphut.swampium.utils.reflections.FieldsFinder
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

/**
 * ProducerWrapper of SwObject class constructor
 */
class SwObjectInjectableWrapper<T : Any>(
        val swObjectClass: KClass<T>,
        override val namePattern: String,
        private val swObjectInstanceManager: SwObjectInstanceManager
) : InjectableWrapper {
    var catchedThrowable: Throwable? = null
    override val disabledReason: Throwable?
        get() = when {
            !fulfilled -> InjectRequirementNotFulfilledException(this, notFulfilledRequirements.toSet())
            else -> catchedThrowable
        }

    override val producer: (requestedType: KType, requestedName: String, requester: InjectableWrapper) -> Any = { _, _, _ -> getInstance() }

    fun getInstance() = swObjectInstanceManager.getInstance(swObjectClass)
            ?: throw IllegalStateException("${swObjectClass.qualifiedName} not yet initialized")

    fun isInitialized() = swObjectInstanceManager.getInstance(swObjectClass) != null

    val resolvedRequirements = HashMap<InjectRequirement, InjectableWrapper>()


    /**
     * The required injectable of the constructor parameters
     */
    private val constructorInjectRequirements = swObjectClass.constructors.first().parameters
            .map(InjectRequirement.Companion::fromParameter)

    /**
     * The required injectable of the properties
     */
    @Suppress("UNCHECKED_CAST")
    private val propertiesInjectRequirements = FieldsFinder.getAllDeclaredPropertyRecursively(swObjectClass)
            .asSequence()
            // filter out constructor declared properties
            .filter { property -> !swObjectClass.constructors.first().parameters.any { it.name == property.name } }
            .filter { it.javaField?.isAnnotationPresent(Inject::class.java) ?: false }
            .onEach { if (it !is KMutableProperty<*>) Swampium.logger.error("$productType::${it.name} is annotated with @Inject but is not mutable") }
            .mapNotNull { it as? KMutableProperty1<T, Any> }
            .map { it to InjectRequirement.fromProperty(it) }
            .toList().toMap()

    /**
     * All required injectable
     */
    val injectRequirements get() = constructorInjectRequirements.union(propertiesInjectRequirements.values)


    override val productType: KType
        get() {
            return swObjectClass.createType()
        }

    /**
     * Construct the SwObject instance
     * @throws RequiredInjectableCannotBeActiveException
     */
    fun constructSwObjectInstance(): T {
        // Check fulfilled
        if (notFulfilledRequirements.isNotEmpty())
            throw IllegalStateException("SwObject inject requirements not fulfilled")

        val requiredInjectables = resolvedRequirements.runCatching {
            map { it.key to it.value.producer(it.key.requiredType, it.key.name, this@SwObjectInjectableWrapper) }
                    .toMap()
        }.getOrElse { throw RequiredInjectableCannotBeActiveException(this, it).also { e -> catchedThrowable = e } }


        val instance: T = swObjectClass.constructors.first().runCatching {
            isAccessible = true
            call(*constructorInjectRequirements.map { requiredInjectables[it] }.toTypedArray())
        }.getOrElse { throw RequiredInjectableCannotBeActiveException(this, it).also { e -> catchedThrowable = e } }

        propertiesInjectRequirements.forEach {
            it.key.isAccessible = true;
            it.key.set(instance, requiredInjectables[it.value] ?: error(""))
        }

        swObjectInstanceManager.putInstance(instance)
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
        fun <T : Any> fromSwObjectClass(swObjectClass: KClass<T>, instanceManager: SwObjectInstanceManager) =
                SwObjectInjectableWrapper(swObjectClass,
                        SwObject.fromElement(swObjectClass).name, instanceManager)
    }
}
