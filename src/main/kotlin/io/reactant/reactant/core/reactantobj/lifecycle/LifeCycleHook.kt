package io.reactant.reactant.core.reactantobj.lifecycle


interface LifeCycleHook {
    @JvmDefault
    fun onEnable() {
    }

    @JvmDefault
    fun onDisable() {
    }

    @JvmDefault
    fun onSave() {
    }
}
