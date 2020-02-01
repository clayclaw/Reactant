package dev.reactant.reactant.ui.kits.slot.event

import dev.reactant.reactant.ui.event.AbstractUIElementEvent
import dev.reactant.reactant.ui.kits.slot.ItemStorageElement

class UIElementSlotUpdatedEvent(override val target: ItemStorageElement) : UISlotUpdatedEvent, AbstractUIElementEvent(target)
