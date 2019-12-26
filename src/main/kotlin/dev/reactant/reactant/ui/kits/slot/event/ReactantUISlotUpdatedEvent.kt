package dev.reactant.uikit.element.slot

import dev.reactant.reactant.ui.event.AbstractUIElementEvent

class ReactantUISlotUpdatedEvent(override val target: ItemStorageElement) : UISlotUpdatedEvent, AbstractUIElementEvent(target)
