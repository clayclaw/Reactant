package dev.reactant.rui.dom

import dev.reactant.rui.events.RuiClickEvent
import dev.reactant.rui.events.RuiDragEvent
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

data class BackgroundRenderingInfo(
        val uiX: Int,
        val uiY: Int,
        val innerX: Int,
        val innerY: Int,
        val boxWidth: Int,
        val boxHeight: Int,
)

object RuiSizing {
    const val FillParent = -314159265
    const val FitContent = -271828182 // equal to auto
}

object RuiPositioning {
    const val Auto = -648945751
}

/**
 * Alias of RuiSizing
 */
val RuiWidth = RuiSizing

/**
 * Alias of RuiSizing
 */
val RuiHeight = RuiSizing

enum class RuiDisplay {
    Flex, None
}

enum class RuiVisibility {
    Visible, Hidden
}

enum class RuiFlexDirection {
    Row, RowReverse, Column, ColumnReverse
}

enum class RuiPosition {
    Static, Fixed, Absolute, Relative
}

enum class RuiJustifyContent {
    FlexStart, FlexEnd, Center,
//    SpaceBetween, SpaceAround
}

enum class RuiAlign {
    FlexStart, FlexEnd, Center, Default
}

enum class RuiFlexWrap {
    NoWrap, Wrap, WrapReverse
}

enum class RuiOverflow {
    None, XHidden, YHidden, Hidden
}

enum class RuiInteractEvent {
    Inherit, Auto, None
}

data class RuiDivProps(

        /* DISPLAY ATTRIBUTES */

        val display: RuiDisplay = RuiDisplay.Flex,
        val visibility: RuiVisibility = RuiVisibility.Visible,

        val position: RuiPosition = RuiPosition.Static,
        val top: Int = RuiPositioning.Auto,
        val right: Int = RuiPositioning.Auto,
        val bottom: Int = RuiPositioning.Auto,
        val left: Int = RuiPositioning.Auto,
        val zIndex: Int = RuiPositioning.Auto,

        val width: Int = RuiSizing.FillParent,
        val height: Int = RuiSizing.FitContent,

        val maxWidth: Int = Int.MAX_VALUE,
        val minWidth: Int = 0,

        val maxHeight: Int = Int.MAX_VALUE,
        val minHeight: Int = 0,

        val marginTop: Int = 0,
        val marginRight: Int = 0,
        val marginBottom: Int = 0,
        val marginLeft: Int = 0,

        val paddingTop: Int = 0,
        val paddingRight: Int = 0,
        val paddingBottom: Int = 0,
        val paddingLeft: Int = 0,

        val overflow: RuiOverflow = RuiOverflow.Hidden,

        val flexDirection: RuiFlexDirection = RuiFlexDirection.Row,
        val justifyContent: RuiJustifyContent = RuiJustifyContent.FlexStart,
        val alignItem: RuiAlign = RuiAlign.FlexStart,
        val alignSelf: RuiAlign = RuiAlign.Default,
        val flexWrap: RuiFlexWrap = RuiFlexWrap.Wrap,
        val flexShrink: Boolean = false,
        val flexGrow: Boolean = false,
        /**
         * Return the background item, Air will show nothing while null will consider as transparent
         */
        val background: (BackgroundRenderingInfo) -> ItemStack? = { null },

        val interactEvent: RuiInteractEvent = RuiInteractEvent.Inherit,

        /* EVENT LISTENER */

        val onClick: ((RuiClickEvent) -> Unit)? = null,
        val onClickCapture: ((RuiClickEvent) -> Unit)? = null,

        val onDrag: ((RuiDragEvent) -> Unit)? = null,
        val onDragCapture: ((RuiDragEvent) -> Unit)? = null,

        /* STRUCTURE */

        override val key: Any? = null,
        override var children: List<RuiElement<*>> = listOf()
) : RuiElementProps {
    override fun copy() = copy(children = children) // avoid calling wrong function
}

class RuiDivElement(
        override val props: RuiDivProps
) : RuiDOMElement<RuiDivProps> {
    override val type get() = "div"
}

val RUI_DIV: RuiElementFactory<RuiDivProps, RuiDivElement> = RuiElementFactory { props: RuiDivProps, elements: List<RuiElement<*>> ->
    val newProps = props.copy()
    newProps.children = elements
    RuiDivElement(newProps)
}

// dsl

fun RuiBuilder.div(
        display: RuiDisplay = RuiDisplay.Flex,
        visibility: RuiVisibility = RuiVisibility.Visible,

        position: RuiPosition = RuiPosition.Static,
        top: Int = RuiPositioning.Auto,
        right: Int = RuiPositioning.Auto,
        bottom: Int = RuiPositioning.Auto,
        left: Int = RuiPositioning.Auto,
        zIndex: Int = RuiPositioning.Auto,

        width: Int = RuiSizing.FillParent,
        height: Int = RuiSizing.FitContent,

        maxWidth: Int = Int.MAX_VALUE,
        minWidth: Int = 0,

        maxHeight: Int = Int.MAX_VALUE,
        minHeight: Int = 0,

        marginTop: Int = 0,
        marginRight: Int = 0,
        marginBottom: Int = 0,
        marginLeft: Int = 0,

        paddingTop: Int = 0,
        paddingRight: Int = 0,
        paddingBottom: Int = 0,
        paddingLeft: Int = 0,

        overflow: RuiOverflow = RuiOverflow.Hidden,

        flexDirection: RuiFlexDirection = RuiFlexDirection.Row,
        justifyContent: RuiJustifyContent = RuiJustifyContent.FlexStart,
        alignItem: RuiAlign = RuiAlign.FlexStart,
        alignSelf: RuiAlign = RuiAlign.Default,
        flexWrap: RuiFlexWrap = RuiFlexWrap.Wrap,
        flexShrink: Boolean = false,
        flexGrow: Boolean = false,

        background: (BackgroundRenderingInfo) -> ItemStack? = { null },

        interactEvent: RuiInteractEvent = RuiInteractEvent.Inherit,

        onClick: ((RuiClickEvent) -> Unit)? = null,
        onClickCapture: ((RuiClickEvent) -> Unit)? = null,

        onDrag: ((RuiDragEvent) -> Unit)? = null,
        onDragCapture: ((RuiDragEvent) -> Unit)? = null,

        key: Any? = null,
        childrenBuilder: RuiBuilder.() -> Unit = {}

) = e(RUI_DIV, RuiDivProps(
        display, visibility, position, top, right, bottom, left, zIndex, width, height, maxWidth, minWidth, maxHeight, minHeight, marginTop, marginRight, marginBottom, marginLeft, paddingTop, paddingRight, paddingBottom, paddingLeft, overflow, flexDirection, justifyContent, alignItem, alignSelf, flexWrap, flexShrink, flexGrow, background, interactEvent, onClick, onClickCapture, onDrag, onDragCapture, key
), RuiBuilder().apply(childrenBuilder))

class RuiDivPropsBuilder() : RuiPropsBuilder<RuiDivProps>() {

    var display: RuiDisplay = RuiDisplay.Flex
    var visibility: RuiVisibility = RuiVisibility.Visible

    var position: RuiPosition = RuiPosition.Static
    var top: Int = RuiPositioning.Auto
    var right: Int = RuiPositioning.Auto
    var bottom: Int = RuiPositioning.Auto
    var left: Int = RuiPositioning.Auto
    var zIndex: Int = RuiPositioning.Auto

    var width: Int = RuiSizing.FillParent
    var height: Int = RuiSizing.FitContent

    var maxWidth: Int = Int.MAX_VALUE
    var minWidth: Int = 0

    var maxHeight: Int = Int.MAX_VALUE
    var minHeight: Int = 0

    var marginTop: Int = 0
    var marginRight: Int = 0
    var marginBottom: Int = 0
    var marginLeft: Int = 0

    var paddingTop: Int = 0
    var paddingRight: Int = 0
    var paddingBottom: Int = 0
    var paddingLeft: Int = 0

    var overflow: RuiOverflow = RuiOverflow.Hidden

    var flexDirection: RuiFlexDirection = RuiFlexDirection.Row
    var justifyContent: RuiJustifyContent = RuiJustifyContent.FlexStart
    var alignItem: RuiAlign = RuiAlign.FlexStart
    var alignSelf: RuiAlign = RuiAlign.Default
    var flexWrap: RuiFlexWrap = RuiFlexWrap.Wrap
    var flexShrink: Boolean = false
    var flexGrow: Boolean = false

    /**
     * Return the background item, Air will show nothing while null will consider as transparent
     */
    var background: (BackgroundRenderingInfo) -> ItemStack? = { null }

    var interactEvent: RuiInteractEvent = RuiInteractEvent.Inherit

    /* EVENT LISTENER */

    var onClick: ((RuiClickEvent) -> Unit)? = null
    var onClickCapture: ((RuiClickEvent) -> Unit)? = null

    var onDrag: ((RuiDragEvent) -> Unit)? = null
    var onDragCapture: ((RuiDragEvent) -> Unit)? = null

    /* STRUCTURE */

    var key: Any? = null


    // shorthands

    fun flexRow() {
        this.flexDirection = RuiFlexDirection.Row
    }

    fun flexColumn() {
        this.flexDirection = RuiFlexDirection.Column
    }

    fun positionAbsolute(left: Int, top: Int) {
        this.position = RuiPosition.Absolute
        this.left = left
        this.top = top
    }

    fun positionFixed(left: Int, top: Int) {
        this.position = RuiPosition.Fixed
        this.left = left
        this.top = top
    }

    fun background(itemStack: ItemStack?) {
        this.background = { itemStack?.clone() }
    }

    fun background(material: Material?) {
        this.background = { material?.let { ItemStack(it) } }
    }

    fun background(backgroundFactory: (BackgroundRenderingInfo) -> ItemStack?) {
        this.background = backgroundFactory
    }

    fun size(width: Int, height: Int) {
        this.width = width;
        this.height = height
    }

    fun margin(top: Int, right: Int, bottom: Int, left: Int) {
        marginTop = top
        marginRight = right
        marginBottom = bottom
        marginLeft = left
    }

    fun margin(top: Int, rightLeft: Int, bottom: Int) = margin(top, rightLeft, bottom, rightLeft)
    fun margin(topBottom: Int, rightLeft: Int) = margin(topBottom, rightLeft, topBottom, rightLeft)
    fun margin(all: Int) = margin(all, all, all, all)

    fun padding(top: Int, right: Int, bottom: Int, left: Int) {
        paddingTop = top
        paddingRight = right
        paddingBottom = bottom
        paddingLeft = left
    }

    fun padding(top: Int, rightLeft: Int, bottom: Int) = padding(top, rightLeft, bottom, rightLeft)
    fun padding(topBottom: Int, rightLeft: Int) = padding(topBottom, rightLeft, topBottom, rightLeft)
    fun padding(all: Int) = padding(all, all, all, all)


    override fun createProps(): RuiDivProps {
        return RuiDivProps(
                display, visibility, position, top, right, bottom, left, zIndex, width, height, maxWidth, minWidth, maxHeight, minHeight, marginTop, marginRight, marginBottom, marginLeft, paddingTop, paddingRight, paddingBottom, paddingLeft, overflow, flexDirection, justifyContent, alignItem, alignSelf, flexWrap, flexShrink, flexGrow, background, interactEvent, onClick, onClickCapture, onDrag, onDragCapture, key
        )
    }

}

fun RuiBuilder.div(propsBuilder: RuiDivPropsBuilder.() -> Unit, childrenBuilder: RuiBuilder.() -> Unit = {}) =
        e(RUI_DIV, RuiDivPropsBuilder().apply(propsBuilder).createProps(), RuiBuilder().apply(childrenBuilder))
