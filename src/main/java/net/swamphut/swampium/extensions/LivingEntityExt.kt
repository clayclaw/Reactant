package net.swamphut.swampium.extensions

import org.bukkit.entity.EntityType.*
import org.bukkit.entity.LivingEntity

private val UNDEAD = setOf(
        SKELETON, STRAY, WITHER_SKELETON, WITHER, ZOMBIE, HUSK, ZOMBIE_VILLAGER, PIG_ZOMBIE, DROWNED, ZOMBIE_HORSE,
        SKELETON_HORSE, PHANTOM
)

private val ARTHROPOD = setOf(SPIDER, CAVE_SPIDER, SILVERFISH, ENDERMITE)

fun LivingEntity.isUndead(): Boolean = type in UNDEAD
fun LivingEntity.isArthropod() = type in ARTHROPOD
