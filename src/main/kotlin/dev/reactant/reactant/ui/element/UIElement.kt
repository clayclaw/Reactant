package dev.reactant.reactant.ui.element

import dev.reactant.reactant.ui.UIView
import dev.reactant.reactant.ui.editing.UIElementEditing
import dev.reactant.reactant.ui.event.UIEvent
import dev.reactant.reactant.ui.eventtarget.UIElementEventTarget
import dev.reactant.reactant.ui.query.UIQueryable
import dev.reactant.reactant.ui.rendering.RenderedItems
import io.reactivex.subjects.Subject
import kotlin.math.max
import kotlin.reflect.KClass

interface UIElement : UIElementEventTarget, UIQueryable {
    val view: UIView? get() = parent?.view
    val elementIdentifier: String
    override var parent: UIElement?
    override var id: String?
    val classList: UIElementClassList
    val attributes: UIElementAttributes

    var display: ElementDisplay
    /**
     * Declaring width, -1 as parent's width
     */
    val width: Int
    /**
     * Declaring height, -1 as parent's height
     */
    val height: Int

    /**
     * For container element considering wrapping
     */
    val minimumFreeSpaceWidth get() = max(0, width) + marginLeft + marginRight
    val minimumFreeSpaceHeight get() = max(0, height) + marginTop + marginBottom

    var marginTop: Int
    var marginRight: Int
    var marginBottom: Int
    var marginLeft: Int
    var margin
        get() = listOf(marginTop, marginRight, marginBottom, marginLeft)
        set(value) = expandDirectionalAttributes(value, 0).let {
            marginTop = it[0]; marginRight = it[1]; marginBottom = it[2]; marginLeft = it[3]
        }


    @JvmDefault
    fun matches(selector: String): Boolean = this.parent!!.querySelectorAll(selector).contains(this)

    fun <T : UIEvent> getEventSubject(clazz: KClass<T>): Subject<T>

    companion object {
        const val MATCH_PARENT = -1
        const val WRAP_CONTENT = -2
        const val MARGIN_AUTO = -11

        fun <T : Any> expandDirectionalAttributes(expanding: List<T>, defaultValue: T): List<T> {
            if (expanding.size > 4) throw IllegalArgumentException(
                    "Directional attributes cannot have more than 4 elements, but found $expanding")
            val expanded = arrayListOf(defaultValue, defaultValue, defaultValue, defaultValue)
            expanding.getOrNull(0)?.let { value -> (0..3).forEach { expanded[it] = value } }
            expanding.getOrNull(1)?.let { value -> setOf(1, 3).forEach { expanded[it] = value } }
            expanding.getOrNull(2)?.let { value -> expanded[2] = value }
            expanding.getOrNull(3)?.let { value -> expanded[3] = value }
            return expanded
        }
    }

    /**
     * the parameters is use to represent how many free space the parent element would like you to fill
     * Useful for MATCH_PARENT
     * You could having size larger than the free space, while it is also representing WRAP_CONTENT
     */
    fun render(parentFreeSpaceWidth: Int, parentFreeSpaceHeight: Int): RenderedItems

    fun edit(): UIElementEditing<UIElement>;
}
