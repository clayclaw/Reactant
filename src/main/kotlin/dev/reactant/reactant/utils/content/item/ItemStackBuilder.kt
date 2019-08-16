@file:Suppress("DEPRECATION")

package dev.reactant.reactant.utils.content.item

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.material.MaterialData

private typealias ItemMetaModifier = ItemMeta.() -> Unit

class ItemStackBuilder {
    lateinit var type: Material
    var amount: Int = 1
    var data: MaterialData? = null
    private var metaModifier: ItemMetaModifier = {}
    private var enchantments = hashMapOf<Enchantment, Int>()

    fun itemMeta(modifier: ItemMetaModifier) {
        this.metaModifier = modifier
    }

    inner class EnchantmentsDeclarations {
        infix fun Enchantment.level(level: Int) {
            enchantments[this] = level
        }
    }

    /**
     * All of the enchantments will add as Unsafe enchantment
     */
    fun enchantments(declare: EnchantmentsDeclarations.() -> Unit) {
        EnchantmentsDeclarations().apply(declare)
    }


    fun build() = ItemStack(type, amount).also {
        it.data = data
        it.itemMeta = it.itemMeta?.apply(metaModifier)
        it.addUnsafeEnchantments(enchantments)
    }
}

fun createItemStack(type: Material = Material.AIR, amount: Int = 1, builderConfig: ItemStackBuilder.() -> Unit = {}): ItemStack {
    return ItemStackBuilder().also {
        it.type = type;
        it.amount = amount
    }.apply(builderConfig).build()
}

