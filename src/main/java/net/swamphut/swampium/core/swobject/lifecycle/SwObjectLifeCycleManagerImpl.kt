package net.swamphut.swampium.core.swobject.lifecycle


import net.swamphut.swampium.core.Swampium
import net.swamphut.swampium.core.exception.lifecycle.LifeCycleActionException
import net.swamphut.swampium.core.exception.lifecycle.RequiredServiceNotActivedException
import net.swamphut.swampium.core.swobject.SwObjectInfo
import net.swamphut.swampium.core.swobject.SwObjectManager
import net.swamphut.swampium.core.swobject.SwObjectState.*
import net.swamphut.swampium.core.swobject.container.ContainerManager
import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.container.SwampiumContainerManager
import net.swamphut.swampium.core.swobject.dependency.ServiceProvider
import net.swamphut.swampium.core.swobject.dependency.ServiceProviderInfoImpl
import net.swamphut.swampium.core.swobject.dependency.ServiceProviderManager
import net.swamphut.swampium.core.swobject.dependency.resolve.ServiceDependencyResolver
import net.swamphut.swampium.core.swobject.instance.InstanceManager
import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleControlAction.*
import java.util.logging.Level

@SwObject
@ServiceProvider(provide = [SwObjectLifeCycleManager::class])
class SwObjectLifeCycleManagerImpl : SwObjectLifeCycleManager {

    private val instanceManager: InstanceManager = Swampium.instance.instanceManager
    private val containerManager: ContainerManager = instanceManager.getInstance(SwampiumContainerManager::class.java)
    private val swObjectManager = instanceManager.getInstance(SwObjectManager::class.java);
    private val serviceProviderManager = instanceManager.getInstance(ServiceProviderManager::class.java);

    override fun invokeAction(swObjectInfo: SwObjectInfo<Any>, action: LifeCycleControlAction): Boolean {

        when (swObjectInfo.state) {

            Unsolved -> throw IllegalStateException("Unsolved object cannot invoke any action: ${swObjectInfo.instance.javaClass}")
            Inactive -> if (action == Disable || action == Save) throw IllegalStateException()
            Active -> if (action == Initialize) throw java.lang.IllegalStateException()
        }

        val inspectors = swObjectManager.swObjectClassMap.values
                .filter { it.state == Active }
                .map { it.instance }
                .filter { HookInspector::class.java.isAssignableFrom(it.javaClass) }
                .map { it as HookInspector }
                .toSet()

        try {

            // Throw if required service not actived
            swObjectInfo.requiredServicesResolvedResult.values.filter { it.state != Active }.let {
                if (!it.isEmpty()) throw RequiredServiceNotActivedException(swObjectInfo, action, it)
            }

            when (action) {
                Initialize -> {
                    inspectors.forEach { inspector -> inspector.beforeInit(swObjectInfo) }
                    if (swObjectInfo.instance is LifeCycleHook) {
                        (swObjectInfo.instance as LifeCycleHook).init()
                    }
                    swObjectInfo.state = Active
                    inspectors.forEach { inspector -> inspector.afterInit(swObjectInfo) }
                }
                Save -> {
                    inspectors.forEach { inspector -> inspector.beforeSave(swObjectInfo) }
                    if (swObjectInfo.instance is LifeCycleHook) {
                        (swObjectInfo.instance as LifeCycleHook).save()
                    }
                    inspectors.forEach { inspector -> inspector.afterSave(swObjectInfo) }
                }
                Disable -> {
                    inspectors.forEach { inspector -> inspector.beforeDisable(swObjectInfo) }
                    if (swObjectInfo.instance is LifeCycleHook) {
                        (swObjectInfo.instance as LifeCycleHook).disable()
                    }
                    swObjectInfo.state = Inactive
                    //reconstruct it
                    inspectors.forEach { inspector -> inspector.afterDisable(swObjectInfo) }
                    Swampium.instance.instanceManager.removeInstance(swObjectInfo.instance.javaClass)
                }
            }
            return true
        } catch (e: LifeCycleActionException) {
            if (swObjectInfo is ServiceProviderInfoImpl) {
                swObjectInfo.lifeCycleActionExceptions.add(e)
            }
        } catch (e: Throwable) {
            if (swObjectInfo is ServiceProviderInfoImpl) {
                swObjectInfo.lifeCycleActionExceptions.add(LifeCycleActionException(swObjectInfo, action, e))
            }
        }
        return false

    }

    private fun triggerInspector(inspector: HookInspector, action: () -> Unit) {
        try {
            action()
        } catch (e: Throwable) {
            Swampium.instance.logger.log(Level.SEVERE, "Throwable catched when trigger hook inspector", e)
        }
    }

    override fun invokeAction(swObjectsInfo: Collection<SwObjectInfo<Any>>, action: LifeCycleControlAction): Boolean {
        swObjectManager.injectAllSwObject()

        val serviceProviders = swObjectsInfo
                .filter { it.instance.javaClass.isAnnotationPresent(ServiceProvider::class.java) }
                .map { serviceProviderManager.serviceClassProvidersInfoMap.getOrElse(it.instance.javaClass, { throw java.lang.IllegalStateException() }) }
                .toSet()
        val resolveResult = ServiceDependencyResolver.resolve(serviceProviders);
        Swampium.instance.logger.log(Level.INFO,
                """

                    Resolved: ${resolveResult.solvedOrder.size}
                    Cyclic nodes: ${resolveResult.cyclicNodesLists}
                    Ignored: ${resolveResult.ignoredNodes.size}

                """.trimIndent())
        val resolvedOrder = ArrayList(resolveResult.solvedOrder.map { it.data }.map {
            @Suppress("UNCHECKED_CAST")
            it as SwObjectInfo<Any>
        })


        // After provider loaded, load sw object
        swObjectsInfo.filter { !it.instance.javaClass.isAnnotationPresent(ServiceProvider::class.java) }
                .let { resolvedOrder.addAll(it) }

        if (action == Disable || action == Save) {
            resolvedOrder.reverse()
        }

        return resolvedOrder
                .filter { it.state == Inactive }
                .map { invokeAction(it, action) }
                .fold(true) { prev, next -> prev && next }
                .also {
                    swObjectManager.swObjectClassMap.values
                            .filter { it.state == Active }
                            .map { it.instance }
                            .filter { HookInspector::class.java.isAssignableFrom(it.javaClass) }
                            .map { it as HookInspector }
                            .forEach { it.afterBulkActionComeplete(action) }
                }
    }

}
