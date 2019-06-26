package io.reactant.reactant.repository.commands

import io.reactant.reactant.extra.command.PermissionNode

internal class RepositoryPermission {
    companion object {
        object Reactant : PermissionNode("Reactant") {
            object REPOSITORY : PermissionNode(child("repo")) {
                object LIST : PermissionNode(child("list"))
                object MODIFY : PermissionNode(child("modify"))
                object RETRIEVE : PermissionNode(child("retrieve"))
            }
        }
    }
}

