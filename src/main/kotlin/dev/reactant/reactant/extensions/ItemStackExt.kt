package dev.reactant.reactant.extensions

import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Material.*
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.FireworkEffectMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta

/**
 * Try to set the color of the item
 * FIREWORK_STAR will lost their effect after color set
 * @return True if success
 */
fun ItemStack.trySetColor(color: Color): Boolean {
    when (this.type) {
        LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS, LEATHER_HORSE_ARMOR -> itemMeta<LeatherArmorMeta> { setColor(color) }
        POTION, SPLASH_POTION, TIPPED_ARROW -> itemMeta<PotionMeta> { setColor(color) }
        FIREWORK_STAR -> itemMeta<FireworkEffectMeta> { effect = FireworkEffect.builder().withColor(color).build() }
        else -> return false
    }
    return true
}

inline fun <reified T : ItemMeta> ItemStack.itemMeta(content: T.() -> Unit) {
    this.itemMeta = (this.itemMeta as T).apply(content)
}
