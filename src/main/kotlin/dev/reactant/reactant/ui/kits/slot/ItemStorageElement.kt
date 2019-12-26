package dev.reactant.uikit.element.slot

import dev.reactant.reactant.ui.element.UIElement

interface ItemStorageElement : UIElement, ItemStorage {
    val quickPutTarget: ItemStorage?
}
