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
import net.swamphut.swampium.core.swobject.dependency.provide.ServiceProvider
import net.swamphut.swampium.core.swobject.dependency.provide.ServiceProviderInfo
import net.swamphut.swampium.core.swobject.dependency.provide.ServiceProviderInfoImpl
import net.swamphut.swampium.core.swobject.dependency.provide.ServiceProviderManager
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

            Unsolved -> throw IllegalStateException("Unsolved object cannot invoke any action: ${swObjectInfo.instanceClass}")
            Inactive -> if (action == Disable || action == Save) throw IllegalStateException()
            Active -> if (action == Initialize) return true;
        }

        try {
            swObjectInfo.requiredServicesResolvedResult.values.filter { it.state != Active }.let {
                if (!it.isEmpty()) throw RequiredServiceNotActivedException(swObjectInfo, action, it)
            }

            when (action) {
                Initialize -> {
                    triggerInspector { inspector -> inspector.beforeInit(swObjectInfo) }
                    if (swObjectInfo.instance is LifeCycleHook) {
                        (swObjectInfo.instance as LifeCycleHook).init()
                    }
                    swObjectInfo.state = Active
                    triggerInspector { inspector -> inspector.afterInit(swObjectInfo) }
                }
                Save -> {
                    triggerInspector { inspector -> inspector.beforeSave(swObjectInfo) }
                    if (swObjectInfo.instance is LifeCycleHook) {
                        (swObjectInfo.instance as LifeCycleHook).save()
                    }
                    triggerInspector { inspector -> inspector.afterSave(swObjectInfo) }
                }
                Disable -> {
                    triggerInspector { inspector -> inspector.beforeDisable(swObjectInfo) }
                    if (swObjectInfo.instance is LifeCycleHook) {
                        (swObjectInfo.instance as LifeCycleHook).disable()
                    }
                    swObjectInfo.state = Inactive
                    triggerInspector { inspector -> inspector.afterDisable(swObjectInfo) }

                    //reconstruct it
                    Swampium.instance.instanceManager.destroyInstance(swObjectInfo.instanceClass)
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

    private fun triggerInspector(action: (HookInspector) -> Unit) {
        getHookInspectors().forEach {
            try {
                it.apply(action)
            } catch (e: Throwable) {
                Swampium.instance.logger.log(Level.SEVERE, "Throwable catched when trigger hook inspector", e)
            }
        }
    }

    override fun invokeAction(swObjectsInfo: Collection<SwObjectInfo<Any>>, action: LifeCycleControlAction): Boolean {
        if (action == Initialize) swObjectManager.injectAllSwObject()

//        val invokingClassesHashSet = swObjectsInfo.map { it.instance::class.java }.toHashSet()

        val serviceProviders = extractServiceProviderInfo(swObjectsInfo)
        val serviceProvidersClasses = serviceProviders.map { it.instanceClass }
        val pureSwObjects = swObjectsInfo.filter { !serviceProvidersClasses.contains(it.instanceClass) }

        val resolveResult = ServiceDependencyResolver.resolve(serviceProviders);
        Swampium.instance.logger.log(Level.INFO, ("Resolved: ${resolveResult.solvedOrder.size}, " +
                "Cyclic nodes: ${resolveResult.cyclicNodesLists}, Ignored: ${resolveResult.ignoredNodes.size}"))
        val resolvedOrder = ArrayList(resolveResult.solvedOrder.map {
            swObjectManager.swObjectClassMap[it.data.instanceClass] ?: throw IllegalStateException()
        })

        // After provider loaded, load pure swobject
        resolvedOrder.addAll(pureSwObjects)

        // execute with reversed order
        if (action == Disable || action == Save) resolvedOrder.reverse()

        return resolvedOrder
//                .filter { invokingClassesHashSet.contains(it.instance::class.java) }
                .map { invokeAction(it, action) }
                .fold(true) { prev, next -> prev && next }
                .also { triggerInspector { it.afterBulkActionComeplete(action) } }
    }

    private fun extractServiceProviderInfo(swObjectsInfo: Collection<SwObjectInfo<Any>>): Set<ServiceProviderInfo<Any>> {
        return swObjectsInfo
                .filter { it.instanceClass.isAnnotationPresent(ServiceProvider::class.java) }
                .map { serviceProviderManager.serviceClassProvidersInfoMap.getOrElse(it.instanceClass, { throw java.lang.IllegalStateException() }) }
                .toSet()
    }


    private fun getHookInspectors(): Set<HookInspector> = swObjectManager.swObjectClassMap.values
            .asSequence()
            .filter { it.state == Active }
            .map { it.instance }
            .filter { HookInspector::class.java.isAssignableFrom(it.javaClass) }
            .map { it as HookInspector }
            .toSet()
}
