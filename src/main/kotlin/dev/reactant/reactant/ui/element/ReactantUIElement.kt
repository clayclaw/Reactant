package dev.reactant.reactant.ui.element

import dev.reactant.reactant.ui.editing.ReactantUIElementEditing
import dev.reactant.reactant.ui.element.collection.ReactantUIElementChildrenSet
import dev.reactant.reactant.ui.element.collection.ReactantUIElementClassSet
import dev.reactant.reactant.ui.element.style.ReactantUIElementStyle
import dev.reactant.reactant.ui.event.UIElementEvent
import dev.reactant.reactant.ui.event.UIEvent
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlin.reflect.KClass


abstract class ReactantUIElement(override val elementIdentifier: String) : ReactantUIElementStyle(), UIElement {
    init {
        el = this
    }

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

    override val rootElement: UIElement? get() = parent?.rootElement ?: this

    final override val children = ReactantUIElementChildrenSet(this)

    final override val attributes = HashMap<String, String?>()
    override var id: String? = null//by attributes.withDefault { null }
    override var classList = ReactantUIElementClassSet(attributes)


    private val eventSubjects: HashMap<KClass<out UIEvent>, Subject<out Any>> = HashMap()

    @Suppress("UNCHECKED_CAST")
    override fun <T : UIEvent> getEventSubject(clazz: KClass<T>): Subject<T> =
            eventSubjects.getOrPut(clazz) { PublishSubject.create<T>() } as Subject<T>

    abstract override fun edit(): ReactantUIElementEditing<ReactantUIElement>

    override fun renderVisibleElementsPositions(): LinkedHashMap<out ReactantUIElement, HashSet<Pair<Int, Int>>> = super.renderVisibleElementsPositions() as LinkedHashMap<out ReactantUIElement, HashSet<Pair<Int, Int>>>
}
