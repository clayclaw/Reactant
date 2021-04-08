package dev.reactant.reactant.extra.command.exceptions

import org.bukkit.command.CommandSender
import kotlin.reflect.KClass

class CommandExecutionActorTypeException(override val actor: Any, val allowedSenderType: List<KClass<out CommandSender>>)
    : Exception("Only ${allowedSenderType.map { it.simpleName }.joinToString("/")} can execute this command."), CommandCommonExecutionException
