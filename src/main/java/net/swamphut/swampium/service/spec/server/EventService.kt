package net.swamphut.swampium.service.spec.server

import io.reactivex.Observable
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import kotlin.reflect.KClass

interface EventService {
    fun <T : Event> on(registerSwObject: Any, eventClass: KClass<T>,
                       eventPriority: EventPriority = EventPriority.NORMAL): Observable<T>;

    class Registering(val eventService: EventService, val registerSwObject: Any) {
        inline fun <reified T : Event> KClass<T>.listen(): Observable<T> =
                eventService.on(registerSwObject, this, EventPriority.NORMAL)

        inline infix fun <reified T : Event> KClass<T>.listen(eventPriority: EventPriority): Observable<T> =
                eventService.on(registerSwObject, this, eventPriority)
    }

    fun registerBy(registerSwObject: Any, registering: Registering.() -> Unit) {
        Registering(this, registerSwObject).apply(registering)
    }

}
