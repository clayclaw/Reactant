package net.swamphut.swampium.core.swobject

import net.swamphut.swampium.core.Swampium
import net.swamphut.swampium.core.configs.ServiceSpecifyingConfig
import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.dependency.ServiceProvider
import net.swamphut.swampium.core.swobject.dependency.ServiceProviderManager
import net.swamphut.swampium.core.swobject.dependency.injection.Inject
import net.swamphut.swampium.core.swobject.dependency.injection.LazyInjection
import net.swamphut.swampium.core.swobject.dependency.injection.LazyInjectionImplement
import net.swamphut.swampium.core.swobject.dependency.resolve.ServiceDependencyDecider
import net.swamphut.swampium.utils.reflections.FieldsFinder

@SwObject
@ServiceProvider
class SwObjectManager {
    interface SwObjectEventListener {
        fun beforeRemove(swObjectInfo: SwObjectInfo<Any>) {}
        fun afterRemoved(swObjectInfo: SwObjectInfo<Any>) {}
        fun beforeAdd(swObjectInfo: SwObjectInfo<Any>) {}
        fun afterAdded(swObjectInfo: SwObjectInfo<Any>) {}
    }

    private val _swObjectClassMap: HashMap<Class<*>, SwObjectInfo<Any>> = HashMap()
    val swObjectClassMap: Map<Class<*>, SwObjectInfo<Any>> = _swObjectClassMap

    private val swObjectEventListeners = HashSet<SwObjectEventListener>()

    fun addSwObject(swObjectInfo: SwObjectInfo<Any>) {
        if (_swObjectClassMap.containsKey(swObjectInfo.instance.javaClass)) throw IllegalArgumentException()
        swObjectEventListeners.forEach { it.beforeAdd(swObjectInfo) }
        _swObjectClassMap[swObjectInfo.instance.javaClass] = swObjectInfo
        swObjectEventListeners.forEach { it.afterAdded(swObjectInfo) }
    }

    fun removeSwObject(swObjectInfo: SwObjectInfo<Any>) {
        if (!_swObjectClassMap.containsKey(swObjectInfo.instance.javaClass)) throw IllegalArgumentException()
        swObjectEventListeners.forEach { it.beforeRemove(swObjectInfo) }
        _swObjectClassMap.remove(swObjectInfo.instance.javaClass)
        swObjectEventListeners.forEach { it.afterRemoved(swObjectInfo) }
    }

    fun addSwObjectEventListener(listener: SwObjectEventListener) {
        swObjectEventListeners.add(listener)
    }

    fun removeSwObjectEventListener(listener: SwObjectEventListener) {
        swObjectEventListeners.remove(listener)
    }

    fun getSwObjectsByAnnotation(annotation: Class<out Annotation>): Set<SwObjectInfo<Any>> {
        return swObjectClassMap.entries
                .filter { it.key.isAnnotationPresent(annotation) }
                .map { it.value }
                .toSet()
    }

    fun injectAllSwObject() {
        val dependencyDecider = ServiceDependencyDecider(ServiceSpecifyingConfig(),
                Swampium.instance.instanceManager.getInstance(ServiceProviderManager::class.java))

        swObjectClassMap.values
                .filter { it.state == SwObjectState.Unsolved }
                .asSequence()
                .onEach { swObjectInfo ->
                    swObjectInfo.requiredServices.forEach { requiredService ->
                        dependencyDecider.getDecided(swObjectInfo.instance::class.java, requiredService)
                                .let { decided ->
                                    if (decided != null) {
                                        swObjectInfo.requiredServicesResolvedResult[requiredService] = decided
                                        decided.requester.add(swObjectInfo)
                                    }
                                }
                    }
                }
                .filter { it.fulfilled } // Only inject when fulfilled
                .forEach { swObjectInfo ->
                    swObjectInfo.state = SwObjectState.Inactive
                    FieldsFinder.getAllDeclaredFieldsRecursively(swObjectInfo.instance::class.java)
                            .filter { it.isAnnotationPresent(Inject::class.java) }
                            .onEach { it.isAccessible = true }
                            .forEach {
                                it.set(swObjectInfo.instance, when (it.type) {
                                    LazyInjection::class.java -> LazyInjectionImplement<Any>(swObjectInfo, it)
                                    else -> swObjectInfo.requiredServicesResolvedResult[it.type]!!.instance
                                })
                            }
                }
    }


}
