package dev.reactant.reactant.core.commands

import dev.reactant.reactant.extra.command.PermissionNode

internal class ReactantPermissions {
    companion object {
        object Reactant : PermissionNode("Reactant") {
            object REACTANT_OBJ : PermissionNode(child("reactantobj")) {
                object LIST : PermissionNode(child("list"))
                object STATUS : PermissionNode(child("status"))
            }
        }
    }
}

