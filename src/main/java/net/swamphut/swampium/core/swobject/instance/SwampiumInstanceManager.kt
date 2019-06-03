package net.swamphut.swampium.core.swobject.instance

import net.swamphut.swampium.core.swobject.instance.factory.InstanceFactory
import net.swamphut.swampium.core.swobject.instance.factory.InstanceProductInfo
import java.util.HashMap
import kotlin.collections.HashSet
import kotlin.collections.filter
import kotlin.collections.firstOrNull
import kotlin.collections.forEach
import kotlin.collections.mapNotNull
import kotlin.collections.minusAssign
import kotlin.collections.plusAssign
import kotlin.reflect.KType
import kotlin.reflect.full.isSupertypeOf


class SwampiumInstanceManager : InstanceManager {
    override fun destroyInstance(instance: Any) {
        instanceProductInfos[instance]?.let { instanceMap[it.type] }
    }

    private val instanceFactories: HashSet<InstanceFactory> = HashSet()
    private val instanceMap: HashMap<KType, HashMap<String, Any>> = HashMap()
    private val instanceProductInfos: HashMap<Any, InstanceProductInfo> = HashMap()

    override fun getInstance(type: KType, name: String): Any? {
        fun getExactly() = instanceMap[type]?.get(name)
        fun getSubtype() = instanceMap.filter { type.isSupertypeOf(it.key) }.mapNotNull { it.value[name] }.firstOrNull()
        return getExactly() ?: getSubtype() ?: createInstance(type, name)
    }

    override fun addInstanceFactory(instanceFactory: InstanceFactory) {
        instanceFactories += instanceFactory
    }

    override fun removeInstanceFactory(instanceFactory: InstanceFactory) {
        instanceFactories -= instanceFactory
    }

    private fun createInstance(type: KType, name: String): Any? {
        instanceFactories.forEach { factory -> factory.createInstance(type, name).let { if (it != null) return it } }
        return null
    }

}
