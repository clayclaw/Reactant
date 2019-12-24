package dev.reactant.reactant.core.commands

import dev.reactant.reactant.extra.command.PermissionNode

object ReactantPermissions : PermissionNode("reactant") {
    object ADMIN : PermissionNode("admin") {
        object DEV : PermissionNode("dev") {
            object REACTANT_OBJ : PermissionNode(ADMIN.child("reactantobj")) {
                object LIST : PermissionNode(child("list"))
                object STATUS : PermissionNode(child("status"))
                object VISUALIZE : PermissionNode(child("visualize"))
            }
        }
    }
}
