package net.swamphut.swampium.ui.element.type.sizing

import net.swamphut.swampium.ui.editing.UIElementEditing

interface ResizableElement : HeightResizableElement, WidthResizableElement

interface ResizableElementsEditing<T : ResizableElement>
    : UIElementEditing<T>, HeightResizableElementEditing<T>, WidthResizableElementEditing<T>
