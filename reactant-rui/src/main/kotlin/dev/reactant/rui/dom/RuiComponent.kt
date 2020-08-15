package dev.reactant.rui.dom


typealias RUIComponent<P> = (P) -> RuiElement<*>

class RuiComponentElement<P : Props>(override val type: RUIComponent<P>, override val props: P) : RuiElement<P>

//typealias ExoticComponent = RUIComponent<PropsWithChild>

interface Props {
    var children: List<RuiElement<*>>
    val key: Any?
    fun copy(): Props
}

fun <P : Props> createElement(type: RUIComponent<P>, props: P, vararg children: RuiElement<*>): RuiElement<P> =
        createElement(type, props, children.toList())

fun <P : Props> createElement(type: RUIComponent<P>, props: P, children: Iterable<RuiElement<*>>): RuiElement<P> =
        RuiComponentElement(type, props.copy().also { it.children = children.toList() } as P)

