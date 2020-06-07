package dev.reactant.desk.config.tree.leaf.single.select.option

import java.util.*

/**
 * @param T Option final result value type
 */
interface OptionsContainerDefinition<T, S : OptionsContainerDefinition.SelectFieldOption<T>> {
    interface SelectFieldOption<T> {
        val name: String
        val id: String
        val value: T
    }

    val options: HashMap<String, out SelectFieldOption<T>>

    /**
     * Find out the option of the value
     */
    fun getOptionFromValue(value: T): S

    /**
     * Find out te value of the option uuid
     */
    fun getOptionFromUUID(uuid: String) = options[uuid] ?: throw IllegalArgumentException("No options for $uuid")
}
