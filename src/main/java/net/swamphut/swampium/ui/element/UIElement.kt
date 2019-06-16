package net.swamphut.swampium.ui.element

typealias UIElementAttributes = HashMap<String, String?>
typealias UIElementChildren = MutableSet<UIElement>
typealias UIElementClassList = MutableSet<String>

interface UIElement {
    var parent: UIElement?
    val children: UIElementChildren
    var id: String?
    val classList: UIElementClassList
    val attributes: UIElementAttributes

    @JvmDefault
    fun matches(selector: String) = ElementSelectorChainPart(selector).matches(this)

    fun closest(selector: String)
    fun querySelector(selector: String)
}
