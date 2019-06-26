package io.reactant.reactant.core.reactantobj.lifecycle


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
