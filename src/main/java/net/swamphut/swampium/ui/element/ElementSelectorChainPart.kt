package net.swamphut.swampium.ui.element

/**
 * Element selector wrapper which will breakdown selector string into multiple node
 * For example:
 *
 */
class ElementSelectorChainPart(selector: String) {
    val selectorPart = Regex("[^ ]+( *>)?").findAll(selector).map { it.value }.firstOrNull()
    val followingSelectorParts = selectorPart?.length?.let { selector.drop(it) }
    val next = followingSelectorParts?.let { ElementSelectorChainPart(it) }

    fun matches(element: UIElement): Boolean {
        var checkingElement: UIElement? = element
        reversedSet().forEach {
            while (true) {
                if (checkingElement == null) return false
                val isPartMatched = it.matchesPart(checkingElement!!)
                checkingElement = checkingElement!!.parent
                if (isPartMatched) break;
            }
        }
        return true
    }

    /**
     * Check is the element match the rule of current part
     */
    fun matchesPart(element: UIElement): Boolean {
        TODO()
    }


    /**
     * Find closest to this element
     */
    fun closest(element: UIElement): UIElement? {
        TODO()
    }

    fun reversedSet(): HashSet<ElementSelectorChainPart> = (next?.reversedSet() ?: hashSetOf()).also { it.add(this) }

    companion object {
        val SELECTOR_GROUPING_REGEX = Regex("(?!\$)(?<selectorPart>(?<element>[a-zA-Z0-9]+)?(?<class>(?:\\.[a-zA-Z0-9]+)*)(?<attribute>(?:\\[(?:[a-zA-Z0-9]+?)(?:=\"(?:.*?(?:[^\\\\]|\\\\\\\\|))\")?])+)?) *(?<connector>[> ])? *")
    }
}
