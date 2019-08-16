package dev.reactant.reactant.ui.element.collection

import dev.reactant.reactant.ui.element.UIElementAttributes

class ReactantUIElementClassSet(private val attributes: UIElementAttributes) : MutableSet<String> {

    private val classSet get() = attributes["class"]?.split(" ")?.filter { it != "" }?.toHashSet() ?: hashSetOf()

    override fun add(element: String) = classSet.let { newClassSet ->
        newClassSet.add(element).also { attributes["class"] = newClassSet.joinToString(" ") }
    }

    override fun addAll(elements: Collection<String>) = classSet.let { newClassSet ->
        newClassSet.addAll(elements).also { attributes["class"] = newClassSet.joinToString(" ") }
    }

    override fun clear() {
        attributes.remove("class")
    }

    override fun iterator(): MutableIterator<String> = StringMutableIterator()

    inner class StringMutableIterator(val iterator: MutableIterator<String> = classSet.iterator())
        : MutableIterator<String>, Iterator<String> by iterator {
        private var lastValue: String? = null
        override fun next(): String = iterator.next().also { lastValue = it }
        override fun remove() = iterator.remove().also { this@ReactantUIElementClassSet.remove(lastValue) }
    }

    override fun remove(element: String) = classSet.let { newClassSet ->
        newClassSet.remove(element).also { attributes["class"] = newClassSet.joinToString(" ") }
    }

    override fun removeAll(elements: Collection<String>) = classSet.let { newClassSet ->
        newClassSet.removeAll(elements).also { attributes["class"] = newClassSet.joinToString(" ") }
    }

    override fun retainAll(elements: Collection<String>) = classSet.let { newClassSet ->
        newClassSet.retainAll(elements).also { attributes["class"] = newClassSet.joinToString(" ") }
    }

    override val size: Int get() = classSet.size

    override fun contains(element: String) = classSet.contains(element)

    override fun containsAll(elements: Collection<String>) = classSet.containsAll(elements)

    override fun isEmpty(): Boolean = classSet.isEmpty()

}
