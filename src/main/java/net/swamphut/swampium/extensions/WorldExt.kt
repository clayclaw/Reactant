package net.swamphut.swampium.extensions

import org.bukkit.Bukkit
import java.util.*

fun worldOf(name: String) = Bukkit.getWorld(name)
fun worldOf(uid: UUID) = Bukkit.getWorld(uid)
