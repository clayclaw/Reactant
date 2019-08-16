package dev.reactant.reactant.extra.command

open class PermissionNode(private val _str: String) {
    fun child(str: String) = toString() + str

    override fun toString(): String = _str
}
