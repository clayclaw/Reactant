package io.reactant.reactant.ui.element.type.spacing

import io.reactant.reactant.ui.editing.UIElementEditing
import io.reactant.reactant.ui.element.UIElement
import io.reactant.reactant.ui.element.UIElement.Companion.expandDirectionalAttributes

interface PaddingElement : UIElement {

    var paddingTop: Int
    var paddingRight: Int
    var paddingBottom: Int
    var paddingLeft: Int

    var padding
        get() = listOf(paddingTop, marginRight, marginBottom, marginLeft)
        set(value) = expandDirectionalAttributes(value, 0).let {
            paddingTop = it[0]; paddingRight = it[1]; paddingBottom = it[2]; paddingLeft = it[3]
        }


    override val minimumFreeSpaceWidth: Int get() = super.minimumFreeSpaceWidth + paddingLeft + paddingRight
    override val minimumFreeSpaceHeight: Int get() = super.minimumFreeSpaceHeight + paddingTop + paddingBottom
}

interface PaddingElementEditing<T : PaddingElement> : UIElementEditing<T> {
    var paddingTop: Int
        get() = element.paddingTop
        set(value) {
            element.paddingTop = value
        }
    var paddingRight: Int
        get() = element.paddingRight
        set(value) {
            element.paddingRight = value
        }
    var paddingBottom: Int
        get() = element.paddingBottom
        set(value) {
            element.paddingBottom = value
        }
    var paddingLeft: Int
        get() = element.paddingLeft
        set(value) {
            element.paddingLeft = value
        }
    var padding: List<Int>
        get() = element.padding
        set(value) {
            element.padding = value
        }

    @JvmDefault
    fun padding(vararg padding: Int) {
        this.padding = padding.toList()
    }
}
