package net.swamphut.swampium.core.swobject.container

import net.swamphut.swampium.core.Swampium
import net.swamphut.swampium.core.dependency.DependencyManager
import net.swamphut.swampium.core.dependency.injection.producer.InjectableWrapper
import net.swamphut.swampium.core.dependency.injection.producer.ProvideInjectableWrapper
import net.swamphut.swampium.core.dependency.injection.producer.SwObjectInjectableWrapper

@SwObject
class SwampiumContainerManager : ContainerManager {

    private val swObjectContainerMap = HashMap<String, Container>()

    private val instanceManager = Swampium.instance.instanceManager
    private val dependencyManager = instanceManager.getInstance(DependencyManager::class)!!

    override val containers: Collection<Container> get() = swObjectContainerMap.values
    private val containerInjectableWrapperMap = HashMap<Container, HashSet<InjectableWrapper>>();

    override fun getContainer(identifier: String): Container? {
        return swObjectContainerMap[identifier]
    }

    override fun addContainer(container: Container) {
        if (swObjectContainerMap[container.identifier] != null) {
            throw IllegalArgumentException("SwObject Container with same rawIdentifier already exist: ${container.identifier}")
        }
        swObjectContainerMap[container.identifier] = container
        addAllInjectableWrapper(container)
    }

    private fun addAllInjectableWrapper(container: Container) {
        container.swObjectClasses
                .filter { it.java.isAnnotationPresent(SwObject::class.java) }
                .map { SwObjectInjectableWrapper.fromSwObjectClass(it, instanceManager) }
                .flatMap { listOf(it).union(ProvideInjectableWrapper.findAllFromSwObjectInjectableWrapper(it)) }
                .onEach { containerInjectableWrapperMap.getOrPut(container) { hashSetOf() }.add(it) }
                .forEach(dependencyManager::addDependency)
    }

    override fun removeContainer(container: Container) {
        if (swObjectContainerMap[container.identifier] == null) {
            throw IllegalArgumentException("SwObject Container not exist: ${container.identifier}")
        }
        containerInjectableWrapperMap[container]!!.forEach { dependencyManager.removeDependency(it) }
        containerInjectableWrapperMap.remove(container)
        swObjectContainerMap.remove(container.identifier)
    }

    override fun getContainerProvidedInjectableWrapper(container: Container): Set<InjectableWrapper> =
            containerInjectableWrapperMap[container]!!


}
