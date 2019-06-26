package io.reactant.reactant.core.commands

import io.reactant.reactant.extra.command.PermissionNode

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

