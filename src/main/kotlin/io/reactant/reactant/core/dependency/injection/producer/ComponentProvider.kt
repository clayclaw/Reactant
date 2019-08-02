package io.reactant.reactant.core.dependency.injection.producer

import io.reactant.reactant.core.ReactantCore
import io.reactant.reactant.core.component.Component
import io.reactant.reactant.core.component.instance.ComponentInstanceManager
import io.reactant.reactant.core.dependency.implied.ImpliedDependRelationHelper
import io.reactant.reactant.core.dependency.injection.Inject
import io.reactant.reactant.core.dependency.injection.InjectRequirement
import io.reactant.reactant.core.exception.InjectRequirementNotFulfilledException
import io.reactant.reactant.core.exception.RequiredInjectableCannotBeActiveException
import io.reactant.reactant.utils.reflections.FieldsFinder
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

/**
 * ProducerWrapper of Component class constructor
 */
class ComponentProvider<T : Any>(
        val componentClass: KClass<T>,
        override val namePattern: String,
        private val componentInstanceManager: ComponentInstanceManager
) : Provider {
    var catchedThrowable: Throwable? = null
    override val disabledReason: Throwable?
        get() = when {
            !fulfilled -> InjectRequirementNotFulfilledException(this, notFulfilledRequirements.toSet())
            else -> catchedThrowable
        }

    override val producer: (requestedType: KType, requestedName: String, requester: Provider) -> Any = { _, _, _ -> getInstance() }

    fun getInstance() = componentInstanceManager.getInstance(componentClass)
            ?: throw IllegalStateException("${componentClass.qualifiedName} not yet initialized")

    fun isInitialized() = componentInstanceManager.getInstance(componentClass) != null

    val resolvedRequirements = HashMap<InjectRequirement, Provider>()


    /**
     * The required injectable of the constructor parameters
     */
    private val constructorInjectRequirements = componentClass.constructors.first().parameters
            .map(InjectRequirement.Companion::fromParameter)

    /**
     * The required injectable of the properties
     */
    @Suppress("UNCHECKED_CAST")
    private val propertiesInjectRequirements = FieldsFinder.getAllDeclaredPropertyRecursively(componentClass)
            .asSequence()
            // filter out constructor declared properties
            .filter { property -> !componentClass.constructors.first().parameters.any { it.name == property.name } }
            .filter { it.javaField?.isAnnotationPresent(Inject::class.java) ?: false }
            .onEach { if (it !is KMutableProperty<*>) ReactantCore.logger.error("$productType::${it.name} is annotated with @Inject but is not mutable") }
            .mapNotNull { it as? KMutableProperty1<T, Any> }
            .map { it to InjectRequirement.fromProperty(it) }
            .toList().toMap()

    /**
     * All required injectable
     */
    val injectRequirements: Set<InjectRequirement>
        get() {
            val variableRequirement = constructorInjectRequirements.union(propertiesInjectRequirements.values)
            val impliedRequirement = variableRequirement.map { requirement -> ImpliedDependRelationHelper.getImpliedDependRequirementsRecursively(requirement) }
                    .flatten()
            return variableRequirement.union(impliedRequirement);
        }


    override val productType: KType
        get() {
            return componentClass.createType()
        }

    /**
     * Construct the Component instance
     * @throws RequiredInjectableCannotBeActiveException
     */
    fun constructComponentInstance(): T {
        // Check fulfilled
        if (notFulfilledRequirements.isNotEmpty())
            throw IllegalStateException("Component inject requirements not fulfilled")

        val requiredInjectables = resolvedRequirements.runCatching {
            map { it.key to it.value.producer(it.key.requiredType, it.key.name, this@ComponentProvider) }
                    .toMap()
        }.getOrElse { throw RequiredInjectableCannotBeActiveException(this, it).also { e -> catchedThrowable = e } }


        val instance: T = componentClass.constructors.first().runCatching {
            isAccessible = true
            call(*constructorInjectRequirements.map { requiredInjectables[it] }.toTypedArray())
        }.getOrElse { throw RequiredInjectableCannotBeActiveException(this, it).also { e -> catchedThrowable = e } }

        propertiesInjectRequirements.forEach {
            it.key.isAccessible = true;
            it.key.set(instance, requiredInjectables[it.value] ?: error(""))
        }

        componentInstanceManager.putInstance(instance)
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
        fun <T : Any> fromComponentClass(componentClass: KClass<T>, instanceManager: ComponentInstanceManager) =
                ComponentProvider(componentClass,
                        Component.fromElement(componentClass).name, instanceManager)
    }
}
