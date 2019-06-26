package io.reactant.reactant.extensions

import org.bukkit.plugin.Plugin
import java.net.URL

val Plugin.jarLocation: URL get() = this::class.java.protectionDomain.codeSource.location
