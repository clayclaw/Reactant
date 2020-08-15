package dev.reactant.rui.dom

class RuiBuilder private constructor(
        private val elements: ArrayList<RuiElement<*>>
) : Iterable<RuiElement<*>> by elements {

    constructor() : this(arrayListOf())

    fun <P : Props> append(element: RuiElement<P>): RuiElement<P> = elements.add(element).let { element }

    fun <P : Props> e(type: RuiElementFactory<P, *>, props: P, children: Iterable<RuiElement<*>>) = append(createElement(type, props, children))

    fun <P : Props> e(type: RuiElementFactory<P, *>, props: P, childrenBuilder: RuiBuilder.() -> Unit = {}) =
            e(type, props, RuiBuilder().apply(childrenBuilder))

    fun <P : Props> e(type: RUIComponent<P>, props: P, children: Iterable<RuiElement<*>>) = append(createElement(type, props, children))

    fun <P : Props> e(type: RUIComponent<P>, props: P, childrenBuilder: RuiBuilder.() -> Unit = {}) =
            e(type, props, (RuiBuilder().apply(childrenBuilder)))

}

fun ruiDsl(content: RuiBuilder.() -> RuiElement<*>): RuiElement<*> {
    return content(RuiBuilder())
}


abstract class RuiPropsBuilder<T : Props>() {
    abstract fun createProps(): T
}

