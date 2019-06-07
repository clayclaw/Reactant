package net.swamphut.swampium.core.swobject.lifecycle


import net.swamphut.swampium.core.Swampium
import net.swamphut.swampium.core.dependency.DependencyManager
import net.swamphut.swampium.core.dependency.DependencyRelationManager
import net.swamphut.swampium.core.dependency.injection.producer.SwObjectInjectableWrapper
import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleControlAction.*
import kotlin.reflect.jvm.jvmName

@SwObject
class SwObjectLifeCycleManagerImpl : SwObjectLifeCycleManager {
    private val instanceManager = Swampium.instance.swInstanceManager
    private val relationManager = instanceManager.getOrConstructWithoutInjection(DependencyRelationManager::class)
    private val dependencyManager = instanceManager.getOrConstructWithoutInjection(DependencyManager::class)

    override fun invokeAction(injectableWrapper: SwObjectInjectableWrapper<Any>, action: LifeCycleControlAction): Boolean {
        if (!checkState(injectableWrapper, action)) throw IllegalStateException();
        try {
            triggerInspectors(true, action, injectableWrapper);
            when (action) {
                Initialize -> injectableWrapper.runCatching {
                    constructSwObjectInstance().also {
                        (it as? LifeCycleHook)?.init()
                        instanceManager.putInstance(it)
                    }
                }.onFailure { injectableWrapper.catchedThrowable = it; throw it }
                Save -> (injectableWrapper.getInstance() as? LifeCycleHook)?.save()
                Disable -> {
                    (injectableWrapper.getInstance() as? LifeCycleHook)?.save()
                    instanceManager.destroyInstance(injectableWrapper.getInstance())
                }
            }
            triggerInspectors(false, action, injectableWrapper);
        } catch (e: Throwable) {
            Swampium.logger.error("${injectableWrapper.swObjectClass.jvmName} cannot be initialized", e)
            return false;
        }
        return true;
    }

    private fun triggerInspectors(isBefore: Boolean, action: LifeCycleControlAction, swObjectWrapper: SwObjectInjectableWrapper<Any>) {
        inspectors.forEach {
            if (isBefore) {
                when (action) {
                    Initialize -> it.beforeInit(swObjectWrapper)
                    Save -> it.beforeSave(swObjectWrapper)
                    Disable -> it.beforeDisable(swObjectWrapper)
                }
            } else {
                when (action) {
                    Initialize -> it.afterInit(swObjectWrapper)
                    Save -> it.afterSave(swObjectWrapper)
                    Disable -> it.afterDisable(swObjectWrapper)
                }
            }
        }
    }

    private fun checkState(injectable: SwObjectInjectableWrapper<Any>, action: LifeCycleControlAction): Boolean {
        return when (action) {
            Initialize -> !injectable.isInitialized()
            Save, Disable -> injectable.isInitialized()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun invokeAction(injectables: Collection<SwObjectInjectableWrapper<Any>>, action: LifeCycleControlAction): Boolean {
        val executingInjectables = when (action) {
            Save, Disable -> injectables.flatMap { setOf(it).union(relationManager.getDependencyChildrenRecursively(it)) }
            else -> injectables
        }
        var invokeOrder = relationManager.getDependenciesDepth()
                .filter { executingInjectables.contains(it.key) }
                .map { it.toPair() }
                .sortedBy { it.second }
                .map { it.first }
                .mapNotNull { it as? SwObjectInjectableWrapper<Any> }
                .filter { checkState(it, action) }
        if (action == Disable || action == Save) invokeOrder = invokeOrder.reversed()

        return invokeOrder.map { invokeAction(it, action) }
                .fold(true) { result, next -> result && next }
    }

    private val inspectors = dependencyManager.dependencies.mapNotNull { it as? SwObjectInjectableWrapper<*> }
            .filter { it.isInitialized() }
            .mapNotNull { it.getInstance() as? HookInspector }
}
