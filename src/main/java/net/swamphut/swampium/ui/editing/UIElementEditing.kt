package net.swamphut.swampium.ui.editing

import io.reactivex.Observable
import net.swamphut.swampium.ui.element.ElementDisplay
import net.swamphut.swampium.ui.element.UIElement
import net.swamphut.swampium.ui.element.UIElementAttributes
import net.swamphut.swampium.ui.element.UIElementClassList
import net.swamphut.swampium.ui.event.interact.element.UIElementClickEvent

interface UIElementEditing<out T : UIElement> {
    val element: T;
    var id: String?
    var classList: UIElementClassList
    var attributes: UIElementAttributes
    var display: ElementDisplay

    @JvmDefault
    val click: Observable<UIElementClickEvent>
        get() = element.event.filter { it is UIElementClickEvent }.map { it as UIElementClickEvent }
}
