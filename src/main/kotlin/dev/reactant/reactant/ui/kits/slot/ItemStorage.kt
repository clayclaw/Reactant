package dev.reactant.reactant.ui.kits.slot

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.min

interface ItemStorage : Sequence<ItemStack> {

    // Put Item
    /**
     * Transfer the ItemStack to the acceptor
     */
    fun putItem(item: ItemStack, from: ItemStorage?): ItemStack? = putItems(listOf(item), from)[0]

    /**
     * Simulate the transfer result
     * @return The ItemStack which cannot be transferred
     */
    fun testPutItem(item: ItemStack, from: ItemStorage?): ItemStack? = testPutItems(listOf(item), from)[0]

    fun putItems(items: List<ItemStack>, from: ItemStorage?): Map<Int, ItemStack> = putItems(items.mapIndexed { index, itemStack -> index to itemStack }.toMap(), from)

    fun testPutItems(items: List<ItemStack>, from: ItemStorage?): Map<Int, ItemStack> = testPutItems(items.mapIndexed { index, itemStack -> index to itemStack }.toMap(), from)

    /**
     * @return The map that have the Origin index and ItemStacks which cannot be transferred
     */
    fun putItems(items: Map<Int, ItemStack>, from: ItemStorage?): Map<Int, ItemStack>

    fun testPutItems(items: Map<Int, ItemStack>, from: ItemStorage?): Map<Int, ItemStack>

    // Take Item

    fun takeItem(item: ItemStack, from: ItemStorage?): ItemStack? = takeItems(listOf(item), from)[0]

    fun testTakeItem(item: ItemStack, from: ItemStorage?): ItemStack? = testTakeItems(listOf(item), from)[0]

    fun takeItems(wantedItems: List<ItemStack>, from: ItemStorage?): Map<Int, ItemStack> = takeItems(wantedItems.mapIndexed { index, itemStack -> index to itemStack }.toMap(), from)

    fun testTakeItems(wantedItems: List<ItemStack>, from: ItemStorage?): Map<Int, ItemStack> = testTakeItems(wantedItems.mapIndexed { index, itemStack -> index to itemStack }.toMap(), from)

    fun takeItems(wantedItems: Map<Int, ItemStack>, from: ItemStorage?): Map<Int, ItemStack>

    fun testTakeItems(wantedItems: Map<Int, ItemStack>, from: ItemStorage?): Map<Int, ItemStack>

}

class PlayerItemStorage(val player: Player) : ItemStorage {
    private fun putItems(items: Map<Int, ItemStack>, from: ItemStorage?, isTest: Boolean): Map<Int, ItemStack> {
        val inventoryMap = player.inventory.take(36).mapIndexed { index, itemStack -> index to itemStack?.clone() }.toMap().toMutableMap()
        val returnedItems = hashMapOf<Int, ItemStack>()
        items.forEach { (index, itemStack) ->
            val waitForPut = itemStack.clone()
            inventoryMap.entries.reversed().forEach bagLoop@{ (inventoryIndex, inventoryItem) ->
                if (waitForPut.type == Material.AIR || waitForPut.amount == 0) return@bagLoop
                if (inventoryItem == null || inventoryItem.type == Material.AIR) {
                    inventoryMap[inventoryIndex] = waitForPut.clone()
                    waitForPut.amount = 0
                } else if (inventoryItem.isSimilar(waitForPut)) {
                    val puttingAmount = (min(waitForPut.type.maxStackSize - inventoryItem.amount, waitForPut.amount)).coerceAtLeast(0)
                    inventoryItem.amount += puttingAmount
                    waitForPut.amount -= puttingAmount
                }
            }
            if (waitForPut.amount > 0) returnedItems[index] = waitForPut
        }
        if (!isTest) {
            inventoryMap.forEach { (i, itemStack) ->
                if (!(player.inventory.getItem(i)?.let { it.isSimilar(itemStack) && it.amount == itemStack?.amount }
                                ?: (itemStack == null))) {
                    player.inventory.setItem(i, itemStack)
                }
            }
        }
        return returnedItems
    }

    override fun putItems(items: Map<Int, ItemStack>, from: ItemStorage?): Map<Int, ItemStack> = putItems(items, from, false)

    override fun testPutItems(items: Map<Int, ItemStack>, from: ItemStorage?): Map<Int, ItemStack> = putItems(items, from, true)

    override fun takeItems(wantedItems: Map<Int, ItemStack>, from: ItemStorage?): Map<Int, ItemStack> {
        TODO("not implemented")
    }

    override fun testTakeItems(wantedItems: Map<Int, ItemStack>, from: ItemStorage?): Map<Int, ItemStack> {
        TODO("not implemented")
    }

    override fun iterator(): Iterator<ItemStack> = player.inventory.filterNotNull().asSequence().iterator()

}
