package net.swamphut.swampium.core.swobject.dependency

import net.swamphut.swampium.core.Swampium
import net.swamphut.swampium.core.configs.ServiceSpecifyingConfig
import net.swamphut.swampium.core.swobject.SwObjectInfo
import net.swamphut.swampium.core.swobject.SwObjectManager
import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.dependency.resolve.ServiceDependencyDecider

@SwObject
@ServiceProvider
class ServiceProviderManager() {

    private val dependencyDecider: ServiceDependencyDecider = ServiceDependencyDecider(ServiceSpecifyingConfig(), this)
    private val _serviceClassProvidersInfoMap: HashMap<Class<*>, ServiceProviderInfo<Any>> = HashMap()
    val serviceClassProvidersInfoMap: Map<Class<*>, ServiceProviderInfo<Any>> get() = _serviceClassProvidersInfoMap

    private val _serviceProvidersMap: HashMap<Class<*>, HashSet<ServiceProviderInfo<*>>> = HashMap()
    val serviceProvidersMap: Map<Class<*>, Set<ServiceProviderInfo<*>>> get() = _serviceProvidersMap


    init {
        Swampium.instance.instanceManager.getInstance(SwObjectManager::class.java).let { swObjectManager ->
            swObjectManager.getSwObjectsByAnnotation(ServiceProvider::class.java).forEach { swObjectInfo ->
                addProvider(swObjectInfo)
            }
            swObjectManager.addSwObjectEventListener(object : SwObjectManager.SwObjectEventListener {
                override fun afterAdded(swObjectInfo: SwObjectInfo<Any>) {
                    if (!swObjectInfo.instance.javaClass.isAnnotationPresent(ServiceProvider::class.java)) return
                    addProvider(swObjectInfo)
                }

                override fun afterRemoved(swObjectInfo: SwObjectInfo<Any>) {
                    if (!swObjectInfo.instance.javaClass.isAnnotationPresent(ServiceProvider::class.java)) return
                    removeProvider(swObjectInfo)
                }
            });
        }
    }

    private fun addProvider(swObjectInfo: SwObjectInfo<Any>) {
        if (serviceClassProvidersInfoMap.containsKey(swObjectInfo.instance.javaClass)) throw IllegalArgumentException()
        val serviceProviderInfo = ServiceProviderInfoImpl(swObjectInfo)

        serviceProviderInfo.instance.javaClass.getAnnotation(ServiceProvider::class.java)
                .provide
                .map { it.javaObjectType }
                .toSet()
                .let { serviceProviderInfo.provide.addAll(it) }

        //Provide itself
        serviceProviderInfo.provide.add(serviceProviderInfo.instance.javaClass)

        _serviceClassProvidersInfoMap.put(serviceProviderInfo.instance.javaClass, serviceProviderInfo)
        serviceProviderInfo.provide.forEach { providing ->
            _serviceProvidersMap.getOrPut(providing, { HashSet() }).also { it.add(serviceProviderInfo) }
        }
    }

    /**
     * Remove the provider class from class map
     */
    private fun removeProvider(swObjectInfo: SwObjectInfo<Any>) {
        if (!serviceClassProvidersInfoMap.containsKey(swObjectInfo.instance.javaClass)) throw IllegalArgumentException()
        val serviceProviderInfo = serviceClassProvidersInfoMap[swObjectInfo.instance.javaClass]!!
        serviceProviderInfo.provide.forEach { serviceClass ->
            _serviceProvidersMap[serviceClass].let {
                it!!.remove(serviceProviderInfo)
                if (it.isEmpty()) _serviceProvidersMap.remove(serviceClass)
            }
        }
        _serviceClassProvidersInfoMap.remove(swObjectInfo.instance.javaClass)
    }


    /**
     * Find all available provider for a service class, ordered by provider classes' canonical name
     */
    fun <T : Any> findPossibleProvider(serviceClass: Class<T>): Set<ServiceProviderInfo<T>> {
        @Suppress("UNCHECKED_CAST")
        return ((serviceProvidersMap[serviceClass] ?: setOf())
                .sortedBy { serviceClass.canonicalName }
                .toSet() as Set<ServiceProviderInfo<T>>)
    }

}
