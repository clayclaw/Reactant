package net.swamphut.swampium.ui.element.type.sizing

import net.swamphut.swampium.ui.creation.UIElementCreation
import net.swamphut.swampium.ui.element.UIElement

/**
 * Add ability to change the height of this element
 */
interface HeightResizableElement : UIElement {
    override var height: Int
}

interface HeightResizableElementCreation<T : HeightResizableElement> : UIElementCreation<T> {
    @JvmDefault
    var height: Int
        get() = element.height
        set(value) {
            element.height = value
        }
}
