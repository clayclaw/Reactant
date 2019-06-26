package io.reactant.reactant.extra.command.exceptions

class CommandExecutionPermissionException(val actor: Any, val missingPermission: String, val action: String)
    : Exception("${actor.toString()} missing permission \"$missingPermission\" to \"$action\"")