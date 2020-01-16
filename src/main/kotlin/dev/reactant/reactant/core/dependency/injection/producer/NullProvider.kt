package dev.reactant.reactant.core.dependency.injection.producer

import dev.reactant.reactant.core.ReactantCore
import dev.reactant.reactant.core.component.container.Container
import dev.reactant.reactant.core.component.container.ContainerManager
import kotlin.reflect.KType
import kotlin.reflect.full.createType

object NullProvider : Provider {
    override val productType: KType = Any::class.createType(nullable = true)
    override val namePattern: String = ".*"
    override val disabledReason: Throwable? = null
    override val producer: (requestedType: KType, requestedName: String, requester: Provider) -> Any? = { _, _, _ -> null }
    override val container: Container
        get() = ReactantCore.instance.instanceManager.getInstance(ContainerManager::class)!!.getContainer("bk:Reactant")!!
}
