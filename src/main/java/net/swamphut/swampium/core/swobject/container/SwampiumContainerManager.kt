package net.swamphut.swampium.core.swobject.container

import net.swamphut.swampium.core.Swampium
import net.swamphut.swampium.core.dependency.InjectionAnnotationReader
import net.swamphut.swampium.core.dependency.provide.ServiceProvider
import net.swamphut.swampium.core.swobject.SwObjectInfoImpl
import net.swamphut.swampium.core.swobject.SwObjectManager
import net.swamphut.swampium.core.swobject.SwObjectState
import net.swamphut.swampium.core.swobject.instance.factory.SwObjectClassConstructorFactory
import java.util.*
import kotlin.reflect.full.createType

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
            throw IllegalArgumentException("SwObject Container with same rawIdentifier already exist: ${container.identifier}")
        }
        swObjectContainerMap[container.identifier] = container
        val instanceManager = Swampium.instance.swObjectInstanceManager
        val swObjectManager = instanceManager.getInstance(SwObjectManager::class.createType())
        container.swObjectClasses
                .filter { it.java.isAnnotationPresent(SwObject::class.java) }
                .forEach { swObjectClass ->
                    instanceManager.addInstanceFactory(SwObjectClassConstructorFactory(swObjectClass, instanceManager))
                    if (swObjectManager.swObjectClassMap[swObjectClass] != null) {
                        throw IllegalStateException("Duplicated provider class: ${swObjectClass.canonicalName}")
                    }
                    val swObjectInfo = SwObjectInfoImpl(swObjectClass)
                    InjectionAnnotationReader.read(swObjectInfo)
                    swObjectManager.addSwObject(swObjectInfo)
                }
    }

    override fun removeContainer(container: Container) {
        if (swObjectContainerMap[container.identifier] == null) {
            throw IllegalArgumentException("SwObject Container not exist: ${container.identifier}")
        }
        swObjectContainerMap[container.identifier] = container
        Swampium.instance.swObjectInstanceManager.let { instanceManager ->
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
