package dev.reactant.reactant.ui.element

import dev.reactant.reactant.ui.UIDestroyable
import dev.reactant.reactant.ui.UIView
import dev.reactant.reactant.ui.editing.UIElementEditing
import dev.reactant.reactant.ui.element.style.UIElementStyle
import dev.reactant.reactant.ui.event.UIEvent
import dev.reactant.reactant.ui.eventtarget.UIElementEventTarget
import dev.reactant.reactant.ui.query.UIQueryable
import io.reactivex.rxjava3.subjects.Subject
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KClass

interface UIElement : UIElementEventTarget, UIQueryable, UIElementStyle, UIDestroyable {
    val view: UIView? get() = parent?.view
    override var parent: UIElement?
    override var id: String?
    val classList: UIElementClassList
    val attributes: UIElementAttributes

    fun <T : UIEvent> getEventSubject(clazz: KClass<T>): Subject<T>


    fun edit(): UIElementEditing<UIElement>

    /**
     * convert view position (x,y) to element relative position (x,y)
     */
    fun getRelativePosition(viewPosition: Pair<Int, Int>): Pair<Int, Int> {
        return viewPosition.first - this.boundingClientRect.left to viewPosition.second - this.boundingClientRect.top
    }

    /**
     * Get the display item at the position, child should be ignored
     * Use null to represent transparent
     */
    fun render(relativePosition: Pair<Int, Int>): ItemStack?

    /**
     * Destroy childrens and itself
     * It will remove itself from parent
     */
    override fun destroy() {
        this.children.toSet().forEach { it.destroy() }
        this.compositeDisposable.dispose()
        this.parent?.children?.remove(this)
    }


    /**
     * Return the visible position of itself and its children
     * Some attribute like "overflow" may probably implement by overriding this function
     * Order is important, the larger index element will "overlap" the previous element
     * Example: [
     *   {#divA, [(0,0), (0,1), (0,2)]}
     * ]
     */
    fun renderVisibleElementsPositions(): LinkedHashMap<out UIElement, HashSet<Pair<Int, Int>>> {
        return children.map { it.renderVisibleElementsPositions() }
                .fold(linkedMapOf(this to boundingClientRect.toPositions().toHashSet())) { sum, next ->
                    sum.putAll(next)
                    sum
                }
    }

}

inline fun <reified T : UIEvent> UIElement.getEventSubject(): Subject<T> = this.getEventSubject(T::class)
