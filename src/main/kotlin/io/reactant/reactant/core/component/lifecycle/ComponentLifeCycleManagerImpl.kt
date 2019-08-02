package io.reactant.reactant.core.component.lifecycle


import io.reactant.reactant.core.ReactantCore
import io.reactant.reactant.core.component.Component
import io.reactant.reactant.core.component.lifecycle.LifeCycleControlAction.*
import io.reactant.reactant.core.dependency.ProviderManager
import io.reactant.reactant.core.dependency.ProviderRelationManager
import io.reactant.reactant.core.dependency.injection.producer.ComponentProvider
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.jvmName

@Component
class ComponentLifeCycleManagerImpl : ComponentLifeCycleManager {
    private val instanceManager = ReactantCore.instance.reactantInstanceManager
    private val relationManager = instanceManager.getOrConstructWithoutInjection(ProviderRelationManager::class)
    private val dependencyManager = instanceManager.getOrConstructWithoutInjection(ProviderManager::class)

    override fun invokeAction(injectableWrapper: ComponentProvider<Any>, action: LifeCycleControlAction): Boolean {
        if (!checkState(injectableWrapper, action)) throw IllegalStateException();
        try {
            triggerInspectors(true, action, injectableWrapper);
            when (action) {
                Initialize -> injectableWrapper.runCatching {
                    constructComponentInstance().also {
                        (it as? LifeCycleHook)?.onEnable()
                        instanceManager.putInstance(it)
                    }
                }.onFailure { injectableWrapper.catchedThrowable = it; throw it }
                Save -> (injectableWrapper.getInstance() as? LifeCycleHook)?.onSave()
                Disable -> {
                    (injectableWrapper.getInstance() as? LifeCycleHook)?.onSave()
                    instanceManager.destroyInstance(injectableWrapper.getInstance())
                }
            }
            triggerInspectors(false, action, injectableWrapper);
        } catch (e: Throwable) {
            ReactantCore.logger.error("${injectableWrapper.componentClass.jvmName} cannot be initialized", e)
            return false;
        }
        return true;
    }

    private fun triggerInspectors(isBefore: Boolean, action: LifeCycleControlAction, componentWrapper: ComponentProvider<Any>) {
        inspectors.forEach {
            if (isBefore) {
                when (action) {
                    Initialize -> it.beforeEnable(componentWrapper)
                    Save -> it.beforeSave(componentWrapper)
                    Disable -> it.beforeDisable(componentWrapper)
                }
            } else {
                when (action) {
                    Initialize -> it.afterEnable(componentWrapper)
                    Save -> it.afterSave(componentWrapper)
                    Disable -> it.afterDisable(componentWrapper)
                }
            }
        }
    }

    private fun checkState(injectable: ComponentProvider<Any>, action: LifeCycleControlAction): Boolean {
        return when (action) {
            Initialize -> !injectable.isInitialized()
            Save, Disable -> injectable.isInitialized()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun invokeAction(injectables: Collection<ComponentProvider<Any>>, action: LifeCycleControlAction): Boolean {
        val executingInjectables = when (action) {
            Save, Disable -> injectables.flatMap { setOf(it).union(relationManager.getDependencyChildrenRecursively(it)) }
            else -> injectables
        }
        var invokeOrder = relationManager.getDependenciesDepth()
                .filter { executingInjectables.contains(it.key) }
                .map { it.toPair() }
                .sortedBy { it.second }
                .map { it.first }
                .mapNotNull { it as? ComponentProvider<Any> }


        invokeOrder = invokeOrder.union(injectables.filter { it.productType.isSubtypeOf(LifeCycleHook::class.createType()) } // All life cycle hook
                .minus(invokeOrder)).toList();

        if (action == Disable || action == Save) invokeOrder = invokeOrder.reversed()

        return invokeOrder
                .filter { checkState(it, action) }
                .map { invokeAction(it, action) }
                .fold(true) { result, next -> result && next }
                .also { inspectors.forEach { it.afterBulkActionComplete(action) } }
    }

    private val inspectors
        get() = dependencyManager.providers.mapNotNull { it as? ComponentProvider<*> }
                .filter { it.isInitialized() }
                .mapNotNull { it.getInstance() as? LifeCycleInspector }
}
