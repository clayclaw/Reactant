package io.reactant.reactant.ui.element.type.sizing

import io.reactant.reactant.ui.editing.UIElementEditing

interface ResizableElement : HeightResizableElement, WidthResizableElement

interface ResizableElementsEditing<T : ResizableElement>
    : UIElementEditing<T>, HeightResizableElementEditing<T>, WidthResizableElementEditing<T>
