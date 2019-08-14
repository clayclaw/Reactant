package io.reactant.reactant.ui.query

import io.reactant.reactant.ui.element.UIElement
import io.reactant.reactant.ui.element.UIElementChildren

interface UIQueryable {
    val children: UIElementChildren

    /**
     * Find by predicate, return first or null
     */
    fun UIQueryable.firstElement(predicate: (UIElement) -> Boolean): UIElement? =
            children.firstOrNull(predicate) ?: children.mapNotNull { it.firstElement(predicate) }.firstOrNull()

    fun getUIElementById(id: String): UIElement? = firstElement { it.id == id }
}

inline fun <reified T : UIElement> UIQueryable.getElementById(id: String): T? = getUIElementById(id) as T?
