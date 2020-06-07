package dev.reactant.desk.config.tree.leaf.single.select.option

import java.util.*

interface ProviderOptionsContainerDefinition<T> :
        OptionsContainerDefinition<T, ProviderOptionsContainerDefinition.SelectFieldOption<T>> {

    data class SelectFieldOption<T>(
            override val name: String,
            override val id: String,
            val valueProvider: () -> T,
            val valuePredicate: (T) -> Boolean
    ) : OptionsContainerDefinition.SelectFieldOption<T> {
        override val value: T get() = valueProvider()
    }

    override val options: HashMap<String, SelectFieldOption<T>>

    /**
     * Add a option with name, value provider and value predicate
     * @param valueProvider Provide the value to set the property when saving
     * @param valuePredicate Check whether the value match the option or not
     */
    fun option(name: String, valueProvider: () -> T, valuePredicate: (T) -> Boolean) {
        val randomId = UUID.randomUUID().toString()
        options[randomId] = SelectFieldOption(name, randomId, valueProvider, valuePredicate)
    }

    /**
     * Match the value with all predicate until first match to find out the corresponding option
     * Complexity: O(n)
     */
    override fun getOptionFromValue(value: T) = options.values.firstOrNull { it.valuePredicate(value) }
            ?: throw IllegalArgumentException("No option predicate match: $value")
}
