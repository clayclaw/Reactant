package io.reactant.reactant.service.spec.server

import io.reactant.reactant.service.spec.dsl.Registrable
import io.reactivex.Observable
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import kotlin.reflect.KClass

interface EventService : Registrable<EventService.Registering> {
    fun <T : Event> on(registerReactantObject: Any, eventClass: KClass<T>,
                       eventPriority: EventPriority = EventPriority.NORMAL): Observable<T>;

    class Registering(val eventService: EventService, val registerReactantObject: Any) {
        @Deprecated("confusing name", ReplaceWith("observable()"))
        inline fun <reified T : Event> KClass<T>.listen() = observable()

        @Deprecated("confusing name", ReplaceWith("observable(eventPriority)"))
        inline infix fun <reified T : Event> KClass<T>.listen(eventPriority: EventPriority) = observable(eventPriority)

        inline fun <reified T : Event> KClass<T>.observable(eventPriority: EventPriority = EventPriority.NORMAL) =
                eventService.on(registerReactantObject, this, eventPriority)


        // Block registering style

        inner class EventRegistering<T : Event>(var eventClass: KClass<T>?,
                                                var eventPriority: EventPriority?,
                                                val consumer: (Observable<T>.() -> Unit)?) {
            fun execute() {
                eventService.on(registerReactantObject, eventClass!!, eventPriority!!).apply(consumer!!)
            }
        }


        inline operator fun <reified T : Event> KClass<T>.invoke(noinline func: Observable<T>.() -> Unit): Unit =
                EventRegistering(this, EventPriority.NORMAL, func).execute()


        inline infix fun <reified T : Event> KClass<T>.priority(eventRegistering: EventRegistering<T>): Unit =
                eventRegistering.also { it.eventClass = this }.execute()

        inline operator fun <reified T : Event> EventPriority.invoke(noinline func: Observable<T>.() -> Unit): EventRegistering<T> =
                EventRegistering(null, this, func)
    }

    override fun registerBy(registerReactantObject: Any, registering: Registering.() -> Unit) {
        Registering(this, registerReactantObject).apply(registering)
    }

}
