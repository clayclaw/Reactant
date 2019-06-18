package net.swamphut.swampium.ui.element.type.sizing

import net.swamphut.swampium.ui.creation.UIElementCreation

interface ResizableElement : HeightResizableElement, WidthResizableElement {
    companion object {
        const val MATCH_PARENT = -1
    }
}

interface ResizableElementsCreation<T : ResizableElement>
    : UIElementCreation<T>, HeightResizableElementCreation<T>, WidthResizableElementCreation<T>
