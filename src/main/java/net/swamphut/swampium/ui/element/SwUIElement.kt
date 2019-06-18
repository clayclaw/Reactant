package net.swamphut.swampium.ui.element

import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import net.swamphut.swampium.ui.element.collection.SwUIElementChildrenSet
import net.swamphut.swampium.ui.element.collection.SwUIElementClassSet
import net.swamphut.swampium.ui.element.type.sizing.ClickableElement
import net.swamphut.swampium.ui.event.UIEvent
import kotlin.reflect.KClass


abstract class SwUIElement(override val elementIdentifier: String) : UIElement, ClickableElement {
    private var _parent: UIElement? = null
    override var parent: UIElement?
        get() = _parent
        set(newParent) {
            if (newParent == _parent) return
            val originParent = _parent
            _parent = newParent

            originParent?.children?.remove(this)
            newParent?.children?.add(this)
        }

    @Suppress("LeakingThis")
    override val children = SwUIElementChildrenSet(this)

    final override val attributes = HashMap<String, String?>()

    override var id: String? by attributes

    override var classList = SwUIElementClassSet(attributes)

    override var display: ElementDisplay = ElementDisplay.INLINE_BLOCK


    private val eventSubjects: HashMap<KClass<out UIEvent>, Subject<out Any>> = HashMap()

    @Suppress("UNCHECKED_CAST")
    override fun <T : UIEvent> getEventSubject(clazz: KClass<T>): Subject<T> =
            eventSubjects.getOrPut(clazz) { PublishSubject.create<T>() } as Subject<T>
}
