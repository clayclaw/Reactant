package dev.reactant.uikit.element.slot

import dev.reactant.reactant.ui.event.UICancellableEvent
import dev.reactant.reactant.ui.event.UIElementEvent

interface UISlotSwapItemEvent : UIElementEvent, UICancellableEvent, ItemStorageElementEvent, UISlotPutItemEvent, UISlotTakeItemEvent
