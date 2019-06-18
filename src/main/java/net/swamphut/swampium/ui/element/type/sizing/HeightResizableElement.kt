package net.swamphut.swampium.ui.element.type.sizing

import net.swamphut.swampium.ui.creation.UIElementCreation
import net.swamphut.swampium.ui.element.UIElement

interface HeightResizableElement : UIElement {
    var height: Int

    companion object {
        const val MATCH_PARENT = -1
    }
}

interface HeightResizableElementCreation<T : HeightResizableElement> : UIElementCreation<T> {
    @JvmDefault
    var height: Int
        get() = element.height
        set(value) {
            element.height = value
        }
}
