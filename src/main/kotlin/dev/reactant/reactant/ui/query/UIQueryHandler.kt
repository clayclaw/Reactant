package dev.reactant.reactant.ui.query

import dev.reactant.reactant.ui.element.UIElement
import dev.reactant.reactant.ui.element.UIElementName
import org.w3c.css.sac.*
import org.w3c.css.sac.Condition.*
import org.w3c.css.sac.Selector.*

fun selectElements(el: UIQueryable, selector: Selector): Set<UIElement> {
    return when (selector.selectorType) {
        SAC_CONDITIONAL_SELECTOR -> (selector as ConditionalSelector).let {
            selectElements(el, it.simpleSelector).filter {
                isElementMatch(it, selector.condition)
            }.toSet()
        }
        SAC_ANY_NODE_SELECTOR -> (selector as SimpleSelector).let {
            el.recursiveChildren
        }
        SAC_ROOT_NODE_SELECTOR -> (selector as SimpleSelector).let {
            el.rootElement?.let { setOf(it) } ?: setOf()
        }
        SAC_NEGATIVE_SELECTOR -> (selector as NegativeSelector).let {
            el.recursiveChildren.minus(selectElements(el, it.simpleSelector))
        }
        SAC_ELEMENT_NODE_SELECTOR -> (selector as ElementSelector).let {
            el.recursiveChildren.filter { children ->
                it.localName == null || children.javaClass.run {
                    isAnnotationPresent(UIElementName::class.java) &&
                            getAnnotation(UIElementName::class.java)
                                    .run {
                                        (it.namespaceURI == null || it.namespaceURI == namespace) && it.localName == name
                                    }
                                    .let {
                                        it
                                    }
                }
            }.toSet()
        }
        SAC_TEXT_NODE_SELECTOR -> (selector as CharacterDataSelector).let {
            throw UnsupportedOperationException("Reactant UI Query do not support text node selector")
        }
        SAC_CDATA_SECTION_NODE_SELECTOR -> (selector as CharacterDataSelector).let {
            throw UnsupportedOperationException("Reactant UI Query do not support cdata node selector")
        }
        SAC_PROCESSING_INSTRUCTION_NODE_SELECTOR -> (selector as ProcessingInstructionSelector).let {
            throw UnsupportedOperationException("Reactant UI Query do not support PIs node selector")
        }
        SAC_COMMENT_NODE_SELECTOR -> (selector as CharacterDataSelector).let {
            throw UnsupportedOperationException("Reactant UI Query do not support comment node selector")
        }
        SAC_PSEUDO_ELEMENT_SELECTOR -> (selector as ElementSelector).let {
            throw UnsupportedOperationException("Reactant UI do not have pseudo element")
        }
        SAC_DESCENDANT_SELECTOR -> (selector as DescendantSelector).let {
            selectElements(el, it.ancestorSelector)
                    .flatMap { children -> selectElements(children, it.simpleSelector) }.toSet()
        }
        SAC_CHILD_SELECTOR -> (selector as DescendantSelector).let {
            selectElements(el, it.ancestorSelector)
                    .flatMap { matchFirstSelectorEl ->
                        selectElements(matchFirstSelectorEl, it.simpleSelector)
                                .filter { nestedChildren -> matchFirstSelectorEl.children.contains(nestedChildren) }
                    }.toSet()
        }
        SAC_DIRECT_ADJACENT_SELECTOR -> (selector as SiblingSelector).let {
            selectElements(el, it.selector).mapNotNull(UIElement::parent).toSet().let { possibleParents ->
                selectElements(el, it.siblingSelector).let { secondSelectorResult ->
                    secondSelectorResult.filter { possibleResult -> possibleParents.contains(possibleResult.rootElement) }
                }
            }.toSet()
        }
        else -> throw java.lang.UnsupportedOperationException("Unknown selector : ${selector.selectorType}")
    }.union(el.children.flatMap { selectElements(it, selector) })
}

fun selectElements(el: UIQueryable, selectors: SelectorList): Set<UIElement> {
    return (0 until selectors.length).toList().map(selectors::item)
            .flatMap { selector: Selector -> selectElements(el, selector) }
            .toSet()
}

fun isElementMatch(el: UIElement, condition: Condition): Boolean {
    return when (condition.conditionType) {
        SAC_AND_CONDITION -> (condition as CombinatorCondition).let {
            isElementMatch(el, it.firstCondition) && isElementMatch(el, it.secondCondition)
        }
        SAC_OR_CONDITION -> (condition as CombinatorCondition).let {
            isElementMatch(el, it.firstCondition) || isElementMatch(el, it.secondCondition)
        }
        SAC_NEGATIVE_CONDITION -> (condition as NegativeCondition).let {
            !isElementMatch(el, it.condition)
        }
        SAC_POSITIONAL_CONDITION -> (condition as PositionalCondition).let {
            el.parent?.children?.indexOf(el)?.let { position ->
                if (it.position >= 0) it.position == position
                else it.position == el.parent!!.children.size - position
            } ?: false
        }
        SAC_ATTRIBUTE_CONDITION -> (condition as AttributeCondition).let {
            when {
                !el.attributes.containsKey(condition.localName) -> false
                !condition.specified -> true
                else -> el.attributes[condition.localName]!! == condition.value
            }
        }
        SAC_ID_CONDITION -> (condition as AttributeCondition).let {
            el.id == condition.value
        }
        SAC_LANG_CONDITION -> (condition as LangCondition).let {
            throw UnsupportedOperationException("Language conditions are not supported in Reactant UI Query");
        }
        SAC_ONE_OF_ATTRIBUTE_CONDITION -> (condition as AttributeCondition).let {
            when {
                !el.attributes.containsKey(condition.localName) -> false
                condition.value == "" || condition.value.contains(' ') -> false
                else -> el.attributes[condition.localName]!!.matches(Regex(".*\\b${condition.value}\\b.*"))
            }
        }
        SAC_BEGIN_HYPHEN_ATTRIBUTE_CONDITION -> (condition as AttributeCondition).let {
            when {
                !el.attributes.containsKey(condition.localName) -> false
                condition.value == "" || condition.value.contains(' ') -> false
                else -> el.attributes[condition.localName]!!.matches(Regex(".*(^|\$|-)${condition.value}(^|$|-).*"))
            }
        }
        SAC_CLASS_CONDITION -> (condition as AttributeCondition).let {
            el.classList.contains(condition.value)
        }
        SAC_PSEUDO_CLASS_CONDITION -> (condition as AttributeCondition).let {
            throw UnsupportedOperationException("Reactant UI do not have pseudo class")
        }
        SAC_ONLY_CHILD_CONDITION -> (el.parent?.children?.size ?: 1 == 1)
        SAC_ONLY_TYPE_CONDITION -> (el.parent?.children?.count { it.javaClass == el.javaClass } ?: 1 == 1)
        SAC_CONTENT_CONDITION -> (condition as ContentCondition)
                .let { throw TODO("ItemStack pattern matching engine is not yet implemented") }
        else -> throw java.lang.UnsupportedOperationException("Unknown selector condition: ${condition.conditionType}")
    }
}

