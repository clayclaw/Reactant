package net.swamphut.swampium.ui.element

import io.reactivex.subjects.Subject
import net.swamphut.swampium.ui.event.UIEvent
import net.swamphut.swampium.ui.rendering.RenderedItems
import kotlin.reflect.KClass

interface UIElement {
    val elementIdentifier: String
    var parent: UIElement?
    val children: UIElementChildren
    var id: String?
    val classList: UIElementClassList
    val attributes: UIElementAttributes

    var display: ElementDisplay
    val width: Int
    val height: Int

    @JvmDefault
    fun matches(selector: String): Boolean = TODO()

    @JvmDefault
    fun closest(selector: String): UIElement? = TODO()

    @JvmDefault
    fun querySelector(selector: String): UIElement? = TODO()

    @JvmDefault
    fun querySelectorAll(selector: String): Set<UIElement> = TODO()

    fun <T : UIEvent> getEventSubject(clazz: KClass<T>): Subject<T>
    fun render(): RenderedItems
}
