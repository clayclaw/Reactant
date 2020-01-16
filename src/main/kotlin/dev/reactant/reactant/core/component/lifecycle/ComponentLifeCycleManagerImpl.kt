package dev.reactant.reactant.core.component.lifecycle


import dev.reactant.reactant.core.ReactantCore
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleControlAction.*
import dev.reactant.reactant.core.dependency.ProviderManager
import dev.reactant.reactant.core.dependency.ProviderRelationManager
import dev.reactant.reactant.core.dependency.injection.producer.ComponentProvider
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.PrintStream
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.jvm.jvmName

@Component
class ComponentLifeCycleManagerImpl : ComponentLifeCycleManager {
    private val instanceManager = ReactantCore.instance.reactantInstanceManager
    private val relationManager = instanceManager.getOrConstructWithoutInjection(ProviderRelationManager::class)
    private val dependencyManager = instanceManager.getOrConstructWithoutInjection(ProviderManager::class)

    override fun invokeAction(injectableProvider: ComponentProvider<Any>, action: LifeCycleControlAction): Boolean {
        if (!checkState(injectableProvider, action)) throw IllegalStateException();
        try {
            triggerInspectors(true, action, injectableProvider);
            when (action) {
                Initialize -> injectableProvider.runCatching {
                    constructComponentInstance().also {
                        (it as? LifeCycleHook)?.onEnable()
                        instanceManager.putInstance(it)
                    }
                }.onFailure { injectableProvider.catchedThrowable = it; throw it }
                Save -> (injectableProvider.getInstance() as? LifeCycleHook)?.onSave()
                Disable -> {
                    (injectableProvider.getInstance() as? LifeCycleHook)?.onDisable()
                    instanceManager.destroyInstance(injectableProvider.getInstance())
                }
            }
            triggerInspectors(false, action, injectableProvider);
        } catch (e: Throwable) {
            ReactantCore.logger.error("${injectableProvider.componentClass.jvmName} cannot be ${action.name}", e)
            return false;
        }
        return true;
    }

    private fun triggerInspectors(isBefore: Boolean, action: LifeCycleControlAction, componentProvider: ComponentProvider<Any>) {
        inspectors.forEach {
            if (isBefore) {
                when (action) {
                    Initialize -> it.beforeEnable(componentProvider)
                    Save -> it.beforeSave(componentProvider)
                    Disable -> it.beforeDisable(componentProvider)
                }
            } else {
                when (action) {
                    Initialize -> it.afterEnable(componentProvider)
                    Save -> it.afterSave(componentProvider)
                    Disable -> it.afterDisable(componentProvider)
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

        var maxPrintLength = 0
        val out = PrintStream(FileOutputStream(FileDescriptor.out))
        return invokeOrder
                .filter { checkState(it, action) }
                .mapIndexed { i, provider ->
                    "\r> ${action}: ${String.format("%1$-60s", provider.componentClass.qualifiedName?.takeLast(60))} ...  ($i/${invokeOrder.size})\r".let {
                        maxPrintLength = Math.max(maxPrintLength, it.length)
                        out.printf("%${maxPrintLength}s", it)
                    };
                    invokeAction(provider, action)
                }
                .fold(true) { result, next -> result && next }
                .also { inspectors.forEach { it.afterBulkActionComplete(action) } }
    }

    private val inspectors
        get() = dependencyManager.availableProviders.mapNotNull { it as? ComponentProvider<*> }
                .filter { it.isInitialized() }
                .mapNotNull { it.getInstance() as? LifeCycleInspector }
}
