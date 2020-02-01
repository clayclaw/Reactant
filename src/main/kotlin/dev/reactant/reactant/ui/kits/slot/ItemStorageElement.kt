package dev.reactant.reactant.ui.kits.slot

import dev.reactant.reactant.ui.element.UIElement

interface ItemStorageElement : UIElement, ItemStorage {
    val quickPutTarget: ItemStorage?
    val slotIndex: Int
}
