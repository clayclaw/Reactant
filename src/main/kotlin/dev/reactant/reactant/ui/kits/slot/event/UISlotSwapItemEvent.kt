package dev.reactant.reactant.ui.kits.slot.event

import dev.reactant.reactant.ui.event.UICancellableEvent
import dev.reactant.reactant.ui.event.UIElementEvent

interface UISlotSwapItemEvent : UIElementEvent, UICancellableEvent, ItemStorageElementEvent, UISlotPutItemEvent, UISlotTakeItemEvent
