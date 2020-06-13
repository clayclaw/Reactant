package dev.reactant.reactant.core.commands

import dev.reactant.reactant.extra.command.PermissionNode

object ReactantPermissions : PermissionNode("reactant") {
    object ADMIN : PermissionNode("admin") {
        object DEV : PermissionNode("dev") {
            object OBJ : PermissionNode("obj") {
                object LIST : PermissionNode(child("list"))
                object STATUS : PermissionNode(child("status"))
                object VISUALIZE : PermissionNode(child("visualize"))
            }

            object PROFILER : PermissionNode("profiler")
            object I18N : PermissionNode("i18n") {
                object LIST : PermissionNode(child("list"))
                object GENERATE : PermissionNode(child("generate"))
            }
        }
    }
}
