package net.swamphut.swampium.repository.commands

import net.swamphut.swampium.extra.command.PermissionNode

internal class RepositoryPermission {
    companion object {
        object SWAMPIUM : PermissionNode("swampium") {
            object REPOSITORY : PermissionNode(child("repo")) {
                object LIST : PermissionNode(child("list"))
                object MODIFY : PermissionNode(child("modify"))
                object RETRIEVE : PermissionNode(child("retrieve"))
            }
        }
    }
}

