package net.swamphut.swampium.core.swobject.lifecycle


interface LifeCycleHook {
    @JvmDefault
    fun init() {
    }

    @JvmDefault
    fun disable() {
    }

    @JvmDefault
    fun save() {
    }
}
