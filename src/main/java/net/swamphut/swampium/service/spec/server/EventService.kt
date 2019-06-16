package net.swamphut.swampium.service.spec.server

import io.reactivex.Observable
import net.swamphut.swampium.service.spec.dsl.Registrable
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import kotlin.reflect.KClass

interface EventService : Registrable<EventService.Registering> {
    fun <T : Event> on(registerSwObject: Any, eventClass: KClass<T>,
                       eventPriority: EventPriority = EventPriority.NORMAL): Observable<T>;

    class Registering(val eventService: EventService, val registerSwObject: Any) {
        inline fun <reified T : Event> KClass<T>.listen(): Observable<T> =
                eventService.on(registerSwObject, this, EventPriority.NORMAL)

        inline infix fun <reified T : Event> KClass<T>.listen(eventPriority: EventPriority): Observable<T> =
                eventService.on(registerSwObject, this, eventPriority)


        inner class EventRegistering<T : Event>(var eventClass: KClass<T>?,
                                                var eventPriority: EventPriority?,
                                                val consumer: (Observable<T>.() -> Unit)?) {
            fun execute() {
                eventService.on(registerSwObject, eventClass!!, eventPriority!!).apply(consumer!!)
            }
        }


        inline operator fun <reified T : Event> KClass<T>.invoke(noinline func: Observable<T>.() -> Unit): Unit =
                EventRegistering(this, EventPriority.NORMAL, func).execute()


        inline infix fun <reified T : Event> KClass<T>.priority(eventRegistering: EventRegistering<T>): Unit =
                eventRegistering.also { it.eventClass = this }.execute()

        inline operator fun <reified T : Event> EventPriority.invoke(noinline func: Observable<T>.() -> Unit): EventRegistering<T> =
                EventRegistering(null, this, func)
    }

    override fun registerBy(registerSwObject: Any, registering: Registering.() -> Unit) {
        Registering(this, registerSwObject).apply(registering)
    }

}
