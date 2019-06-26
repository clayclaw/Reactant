package io.reactant.reactant.core.reactantobj.lifecycle


import io.reactant.reactant.core.ReactantCore
import io.reactant.reactant.core.dependency.DependencyManager
import io.reactant.reactant.core.dependency.DependencyRelationManager
import io.reactant.reactant.core.dependency.injection.producer.ReactantObjectInjectableWrapper
import io.reactant.reactant.core.reactantobj.container.Reactant
import io.reactant.reactant.core.reactantobj.lifecycle.LifeCycleControlAction.*
import kotlin.reflect.jvm.jvmName

@Reactant
class ReactantObjectLifeCycleManagerImpl : ReactantObjectLifeCycleManager {
    private val instanceManager = ReactantCore.instance.reactantInstanceManager
    private val relationManager = instanceManager.getOrConstructWithoutInjection(DependencyRelationManager::class)
    private val dependencyManager = instanceManager.getOrConstructWithoutInjection(DependencyManager::class)

    override fun invokeAction(injectableWrapper: ReactantObjectInjectableWrapper<Any>, action: LifeCycleControlAction): Boolean {
        if (!checkState(injectableWrapper, action)) throw IllegalStateException();
        try {
            triggerInspectors(true, action, injectableWrapper);
            when (action) {
                Initialize -> injectableWrapper.runCatching {
                    constructReactantObjectInstance().also {
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
            ReactantCore.logger.error("${injectableWrapper.reactantObjectClass.jvmName} cannot be initialized", e)
            return false;
        }
        return true;
    }

    private fun triggerInspectors(isBefore: Boolean, action: LifeCycleControlAction, reactantObjectWrapper: ReactantObjectInjectableWrapper<Any>) {
        inspectors.forEach {
            if (isBefore) {
                when (action) {
                    Initialize -> it.beforeInit(reactantObjectWrapper)
                    Save -> it.beforeSave(reactantObjectWrapper)
                    Disable -> it.beforeDisable(reactantObjectWrapper)
                }
            } else {
                when (action) {
                    Initialize -> it.afterInit(reactantObjectWrapper)
                    Save -> it.afterSave(reactantObjectWrapper)
                    Disable -> it.afterDisable(reactantObjectWrapper)
                }
            }
        }
    }

    private fun checkState(injectable: ReactantObjectInjectableWrapper<Any>, action: LifeCycleControlAction): Boolean {
        return when (action) {
            Initialize -> !injectable.isInitialized()
            Save, Disable -> injectable.isInitialized()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun invokeAction(injectables: Collection<ReactantObjectInjectableWrapper<Any>>, action: LifeCycleControlAction): Boolean {
        val executingInjectables = when (action) {
            Save, Disable -> injectables.flatMap { setOf(it).union(relationManager.getDependencyChildrenRecursively(it)) }
            else -> injectables
        }
        var invokeOrder = relationManager.getDependenciesDepth()
                .filter { executingInjectables.contains(it.key) }
                .map { it.toPair() }
                .sortedBy { it.second }
                .map { it.first }
                .mapNotNull { it as? ReactantObjectInjectableWrapper<Any> }
                .filter { checkState(it, action) }
        if (action == Disable || action == Save) invokeOrder = invokeOrder.reversed()

        return invokeOrder.map { invokeAction(it, action) }
                .fold(true) { result, next -> result && next }
                .also { inspectors.forEach { it.afterBulkActionComplete(action) } }
    }

    private val inspectors
        get() = dependencyManager.dependencies.mapNotNull { it as? ReactantObjectInjectableWrapper<*> }
                .filter { it.isInitialized() }
                .mapNotNull { it.getInstance() as? HookInspector }
}
