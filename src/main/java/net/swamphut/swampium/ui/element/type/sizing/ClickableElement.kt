package net.swamphut.swampium.ui.element.type.sizing

import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import net.swamphut.swampium.ui.creation.UIElementCreation
import net.swamphut.swampium.ui.element.UIElement
import net.swamphut.swampium.ui.event.SwUIClickEvent

interface ClickableElement : UIElement {
    val click: Subject<SwUIClickEvent> get() = getEventSubject(SwUIClickEvent::class).toSerialized()
}

interface ClickableElementCreation<T : ClickableElement> : UIElementCreation<T> {
    @JvmDefault
    val click
        get() = element.click
}
