package io.reactant.reactant.core.component.lifecycle


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
