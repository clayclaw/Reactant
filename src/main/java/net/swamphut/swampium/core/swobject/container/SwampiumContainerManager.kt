package net.swamphut.swampium.core.swobject.container

import net.swamphut.swampium.core.Swampium
import net.swamphut.swampium.core.swobject.SwObjectInfoImpl
import net.swamphut.swampium.core.swobject.SwObjectManager
import net.swamphut.swampium.core.swobject.SwObjectState
import net.swamphut.swampium.core.swobject.dependency.InjectionAnnotationReader
import net.swamphut.swampium.core.swobject.dependency.ServiceProvider
import java.util.*

@SwObject
@ServiceProvider([ContainerManager::class])
class SwampiumContainerManager : ContainerManager {
    private val swObjectContainerMap = HashMap<String, Container>()

    override val containers: Collection<Container> get() = swObjectContainerMap.values

    override fun getContainer(identifier: String): Container? {
        return swObjectContainerMap[identifier]
    }

    override fun addContainer(container: Container) {
        if (swObjectContainerMap[container.identifier] != null) {
            throw IllegalArgumentException("SwObject Container with same identifier already exist: ${container.identifier}")
        }
        swObjectContainerMap[container.identifier] = container
        Swampium.instance.instanceManager.let { instanceManager ->
            val swObjectManager = instanceManager.getInstance(SwObjectManager::class.java)
            container.swObjectClasses
                    .filter { it.isAnnotationPresent(SwObject::class.java) }
                    .forEach { swObjectClass ->
                        if (swObjectManager.swObjectClassMap[swObjectClass] != null) {
                            throw IllegalStateException("Duplicated provider class: ${swObjectClass.canonicalName}")
                        }
                        val swObjectInfo = SwObjectInfoImpl(swObjectClass)
                        InjectionAnnotationReader.read(swObjectInfo)
                        swObjectManager.addSwObject(swObjectInfo)
                    }
        }
    }

    override fun removeContainer(container: Container) {
        if (swObjectContainerMap[container.identifier] == null) {
            throw IllegalArgumentException("SwObject Container not exist: ${container.identifier}")
        }
        swObjectContainerMap[container.identifier] = container
        Swampium.instance.instanceManager.let { instanceManager ->
            val swObjectManager = instanceManager.getInstance(SwObjectManager::class.java)
            container.swObjectClasses
                    .filter { it.isAnnotationPresent(SwObject::class.java) }
                    .map { swObjectManager.swObjectClassMap[it] }
                    .onEach { if (it?.state == SwObjectState.Active) throw IllegalStateException("Service is still active") }
                    .filter { it != null }
                    .forEach { swObjectManager.removeSwObject(it!!) }
        }
    }
}
