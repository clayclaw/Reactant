package net.swamphut.swampium.ui.creation

import net.swamphut.swampium.ui.element.ElementDisplay
import net.swamphut.swampium.ui.element.UIElement
import net.swamphut.swampium.ui.element.UIElementAttributes
import net.swamphut.swampium.ui.element.UIElementClassList

interface UIElementCreation<out T : UIElement> {
    val element: T;
    var id: String?
    var classList: UIElementClassList
    var attributes: UIElementAttributes
    var display: ElementDisplay
}