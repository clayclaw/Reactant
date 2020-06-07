package dev.reactant.desk.config.tree.leaf.single.select.option

import java.util.*

interface StaticOptionsContainerDefinition<T> :
        OptionsContainerDefinition<T, StaticOptionsContainerDefinition.SelectFieldOption<T>> {

    data class SelectFieldOption<T>(
            override val name: String, override val id: String,
            @Transient override val value: T
    ) : OptionsContainerDefinition.SelectFieldOption<T>

    override val options: HashMap<String, SelectFieldOption<T>>
    val valueOptionsMap: HashMap<T, SelectFieldOption<T>>

    /**
     * Add a option with name and value provider
     */
    fun option(name: String, value: T, forceRandomId: Boolean = false) {
        val optionId = if (forceRandomId) UUID.randomUUID().toString() else when (value) {
            is String, is Number, is UUID -> value as String
            is Enum<*> -> value.name
            else -> UUID.randomUUID().toString()
        }
        val option = SelectFieldOption(name, optionId, value)
        options[optionId] = option
        valueOptionsMap[value] = option
    }

    override fun getOptionFromValue(value: T): SelectFieldOption<T> = valueOptionsMap[value]
            ?: throw IllegalArgumentException("No option predicate match: $value")
}
