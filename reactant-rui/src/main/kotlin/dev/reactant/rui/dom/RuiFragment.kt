package dev.reactant.rui.dom

data class RuiFragmentProps(
        override val key: String? = null,
        override var children: List<RuiElement<*>> = listOf()
) : RuiElementProps {
    override fun copy() = copy(children = children) // avoid calling wrong function
}

class RuiFragmentDOMElement(
        override val props: RuiFragmentProps
) : RuiDOMElement<RuiFragmentProps> {
    override val type get() = "div"
}

val RUI_FRAGMENT: RuiElementFactory<RuiFragmentProps, RuiFragmentDOMElement> = RuiElementFactory { props, elements ->
    val newProps = props.copy()
    newProps.children = elements
    RuiFragmentDOMElement(newProps)
}

// dsl

fun RuiBuilder.fragment(
        key: String? = null,
        vararg children: RuiElement<*>,
) = e(RUI_FRAGMENT, RuiFragmentProps(key), children.toList())

fun RuiBuilder.fragment(vararg children: RuiElement<*>) = fragment(null, *children)

fun RuiBuilder.fragment(children: () -> List<RuiElement<*>>) = e(RUI_FRAGMENT, RuiFragmentProps(null), children())
