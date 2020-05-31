package dev.reactant.reactant.service.spec.server

import dev.reactant.reactant.service.spec.dsl.Registrable
import io.reactivex.Observable
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import kotlin.reflect.KClass

interface EventService : Registrable<EventService.Registering> {
    val requester: Any

    @Deprecated("Registrant is redundant")
    fun <T : Event> on(componentRegistrant: Any, eventClass: KClass<T>, eventPriority: EventPriority = EventPriority.NORMAL) = on(eventClass, eventPriority)

    @Deprecated("Registrant is redundant")
    fun <T : Event> on(componentRegistrant: Any, eventClass: KClass<T>, ignoreCancelled: Boolean = false, eventPriority: EventPriority = EventPriority.NORMAL) = on(eventClass, ignoreCancelled, eventPriority)

    fun <T : Event> on(eventClass: KClass<T>, ignoreCancelled: Boolean = false, eventPriority: EventPriority = EventPriority.NORMAL): Observable<T>

    fun <T : Event> on(eventClass: KClass<T>, eventPriority: EventPriority = EventPriority.NORMAL): Observable<T> = on(eventClass, false, eventPriority)

    fun pushEvent(event: Event)

    class Registering(val eventService: EventService) {
        inline fun <reified T : Event> KClass<T>.observable(eventPriority: EventPriority = EventPriority.NORMAL) =
                eventService.on(this, false, eventPriority)

        inline fun <reified T : Event> KClass<T>.observable(ignoreCancelled: Boolean, eventPriority: EventPriority = EventPriority.NORMAL) =
                eventService.on(this, ignoreCancelled, eventPriority)
    }

    operator fun invoke(registering: Registering.() -> Unit) {
        Registering(this).apply(registering)
    }

    override fun registerBy(componentRegistrant: Any, registering: Registering.() -> Unit) {
        Registering(this).apply(registering)
    }

}
