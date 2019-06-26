package io.reactant.reactant.core.reactantobj.container

import io.reactant.reactant.core.ReactantCore
import io.reactant.reactant.core.dependency.DependencyManager
import io.reactant.reactant.core.dependency.injection.producer.InjectableWrapper
import io.reactant.reactant.core.dependency.injection.producer.ProvideInjectableWrapper
import io.reactant.reactant.core.dependency.injection.producer.ReactantObjectInjectableWrapper

@Reactant
class ReactantContainerManager : ContainerManager {

    private val reactantObjectContainerMap = HashMap<String, Container>()

    private val instanceManager = ReactantCore.instance.instanceManager
    private val dependencyManager = instanceManager.getInstance(DependencyManager::class)!!

    override val containers: Collection<Container> get() = reactantObjectContainerMap.values
    private val containerInjectableWrapperMap = HashMap<Container, HashSet<InjectableWrapper>>();

    override fun getContainer(identifier: String): Container? {
        return reactantObjectContainerMap[identifier]
    }

    override fun addContainer(container: Container) {
        if (reactantObjectContainerMap[container.identifier] != null) {
            throw IllegalArgumentException("ReactantObject Container with same rawIdentifier already exist: ${container.identifier}")
        }
        reactantObjectContainerMap[container.identifier] = container
        addAllInjectableWrapper(container)
    }

    private fun addAllInjectableWrapper(container: Container) {
        container.reactantObjectClasses
                .filter { it.java.isAnnotationPresent(Reactant::class.java) }
                .map { ReactantObjectInjectableWrapper.fromReactantObjectClass(it, instanceManager) }
                .flatMap { listOf(it).union(ProvideInjectableWrapper.findAllFromReactantObjectInjectableWrapper(it)) }
                .onEach { containerInjectableWrapperMap.getOrPut(container) { hashSetOf() }.add(it) }
                .forEach(dependencyManager::addDependency)
    }

    override fun removeContainer(container: Container) {
        if (reactantObjectContainerMap[container.identifier] == null) {
            throw IllegalArgumentException("ReactantObject Container not exist: ${container.identifier}")
        }
        containerInjectableWrapperMap[container]!!.forEach { dependencyManager.removeDependency(it) }
        containerInjectableWrapperMap.remove(container)
        reactantObjectContainerMap.remove(container.identifier)
    }

    override fun getContainerProvidedInjectableWrapper(container: Container): Set<InjectableWrapper> =
            containerInjectableWrapperMap[container]!!


}
