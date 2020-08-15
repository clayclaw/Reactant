package dev.reactant.rui.dom

interface RuiElementProps : Props

interface RuiElement<P : Props> {
    val type: Any
    val props: P

    fun printType(): String {
        return when (type) {
            is String -> type as String
            else -> "Component<${props.let { it::class.simpleName }}>}"
        }
    }

    fun print(): String {
        val attr = props.toString().let { " ${it.take(100)}${if (it.length > 100) "..." else ""}" } ?: ""
        if (true && (props as Props?)?.children?.isNotEmpty() ?: false) {
            return """
<${printType()}${attr}>
${(props as Props).children.flatMap { it.print().split("\n") }.map { "    ${it}" }.joinToString("\n")}
</${printType()}>
                """.trimIndent()
        } else {
            return "<${printType()}${attr} />"
        }
    }
}

interface RuiDOMElement<P : Props> : RuiElement<P>

@Suppress("UNCHECKED_CAST")

fun <P : Props> createElement(type: RuiElementFactory<P, out RuiDOMElement<P>>, props: P, vararg children: RuiElement<*>): RuiElement<P> =
        createElement(type, props, children.toList())

fun <P : Props> createElement(type: RuiElementFactory<P, out RuiDOMElement<P>>, props: P, children: Iterable<RuiElement<*>>): RuiElement<P> =
        type(props.copy() as P, children.toList())

fun interface RuiElementFactory<P : Props, T : RuiDOMElement<P>> : Function2<P, List<RuiElement<*>>, T>
