package dev.reactant.reactant.extensions

import org.bukkit.ChatColor

fun String.translateChatColor(altColorChat: Char = '&') = ChatColor.translateAlternateColorCodes(altColorChat, this)
fun String.stripChatColor() = ChatColor.stripColor(this)
