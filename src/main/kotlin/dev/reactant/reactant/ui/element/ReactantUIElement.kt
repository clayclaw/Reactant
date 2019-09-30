package dev.reactant.reactant.ui.element

import dev.reactant.reactant.ui.editing.UIElementEditing
import dev.reactant.reactant.ui.element.collection.ReactantUIElementChildrenSet
import dev.reactant.reactant.ui.element.collection.ReactantUIElementClassSet
import dev.reactant.reactant.ui.event.UIElementEvent
import dev.reactant.reactant.ui.event.UIEvent
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlin.reflect.KClass


abstract class ReactantUIElement(override val elementIdentifier: String) : UIElement {
    override val event = PublishSubject.create<UIElementEvent>()
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
    override val children = ReactantUIElementChildrenSet(this)

    final override val attributes = HashMap<String, String?>()
    override var id: String? = null//by attributes.withDefault { null }
    override var classList = ReactantUIElementClassSet(attributes)

    override var marginTop: Int = 0
    override var marginRight: Int = 0
    override var marginBottom: Int = 0
    override var marginLeft: Int = 0

    override var display: ElementDisplay = ElementDisplay.INLINE_BLOCK

    private val eventSubjects: HashMap<KClass<out UIEvent>, Subject<out Any>> = HashMap()

    @Suppress("UNCHECKED_CAST")
    override fun <T : UIEvent> getEventSubject(clazz: KClass<T>): Subject<T> =
            eventSubjects.getOrPut(clazz) { PublishSubject.create<T>() } as Subject<T>

    abstract override fun edit(): UIElementEditing<ReactantUIElement>
}
