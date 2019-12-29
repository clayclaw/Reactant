package dev.reactant.reactant.extensions

import dev.reactant.reactant.utils.content.item.ItemStackBuilder
import dev.reactant.reactant.utils.content.item.itemStackOf
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

fun Material.toItemStack(amount: Int = 1, builderConfig: ItemStackBuilder.() -> Unit = {}): ItemStack = itemStackOf(this, amount, builderConfig)

