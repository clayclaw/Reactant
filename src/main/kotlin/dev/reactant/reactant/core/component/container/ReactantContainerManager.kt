package dev.reactant.reactant.core.component.container

import dev.reactant.reactant.core.ReactantCore
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.ProviderManager
import dev.reactant.reactant.core.dependency.injection.producer.ComponentProvider
import dev.reactant.reactant.core.dependency.injection.producer.DynamicProvider
import dev.reactant.reactant.core.dependency.injection.producer.Provider

@Component
class ReactantContainerManager : ContainerManager {

    private val componentContainerMap = HashMap<String, Container>()

    private val instanceManager = ReactantCore.instance.instanceManager
    private val dependencyManager = instanceManager.getInstance(ProviderManager::class)!!

    override val containers: Collection<Container> get() = componentContainerMap.values
    private val containerInjectableWrapperMap = HashMap<Container, HashSet<Provider>>();

    override fun getContainer(identifier: String): Container? {
        return componentContainerMap[identifier]
    }

    override fun addContainer(container: Container) {
        if (componentContainerMap[container.identifier] != null) {
            throw IllegalArgumentException("Component Container with same rawIdentifier already exist: ${container.identifier}")
        }
        componentContainerMap[container.identifier] = container
        addAllInjectableWrapper(container)
    }

    private fun addAllInjectableWrapper(container: Container) {
        container.componentClasses
                .filter { it.java.isAnnotationPresent(Component::class.java) }
                .map { ComponentProvider.fromComponentClass(it, instanceManager) }
                .flatMap { listOf(it).union(DynamicProvider.findAllFromComponentInjectableWrapper(it)) }
                .onEach { containerInjectableWrapperMap.getOrPut(container) { hashSetOf() }.add(it) }
                .forEach(dependencyManager::addProvider)
    }

    override fun removeContainer(container: Container) {
        if (componentContainerMap[container.identifier] == null) {
            throw IllegalArgumentException("Component Container not exist: ${container.identifier}")
        }
        containerInjectableWrapperMap[container]!!.forEach { dependencyManager.removeProvider(it) }
        containerInjectableWrapperMap.remove(container)
        componentContainerMap.remove(container.identifier)
    }

    override fun getContainerProvidedInjectableWrapper(container: Container): Set<Provider> =
            containerInjectableWrapperMap[container]!!


}
