package net.swamphut.swampium.core.commands

import net.swamphut.swampium.extra.command.PermissionNode

internal class SwampiumPermissions {
    companion object {
        object SWAMPIUM : PermissionNode("swampium") {
            object SWOBJECT : PermissionNode(child("swobject")) {
                object LIST : PermissionNode(child("list"))
                object STATUS : PermissionNode(child("status"))
            }
        }
    }
}

