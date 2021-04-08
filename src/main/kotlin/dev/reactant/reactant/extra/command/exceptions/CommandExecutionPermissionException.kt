package dev.reactant.reactant.extra.command.exceptions

class CommandExecutionPermissionException(override val actor: Any, val missingPermission: String, val action: String)
    : Exception("You don't have permission to \"$action\""), CommandCommonExecutionException
