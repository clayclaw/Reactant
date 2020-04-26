package dev.reactant.reactant.extra.server

import dev.reactant.reactant.core.ReactantCore
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import dev.reactant.reactant.service.spec.server.EventService
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import kotlin.reflect.KClass


@Component
class ReactantEventService : LifeCycleHook, Listener, EventService {
    /**
     * Map which using Pair<EventClass, Boolean> as key, while the Boolean is ignoreCancelled
     */
    private val eventPrioritySubjectMap = HashMap<Pair<Class<out Event>, Boolean>, HashMap<EventPriority, PublishSubject<Event>>>();
    private val listeningEventClasses = HashSet<Pair<Class<out Event>, Boolean>>();

    override fun onDisable() {
        HandlerList.unregisterAll(this)
        eventPrioritySubjectMap.flatMap { it.value.map { it.value } }.map { it.onComplete() }
    }

    private fun onEvent(event: Event, ignoreCancelled: Boolean, priority: EventPriority) {
        if (eventPrioritySubjectMap.containsKey(event::class.java to ignoreCancelled)
                && eventPrioritySubjectMap[event::class.java to ignoreCancelled]!!.containsKey(priority)) {
            eventPrioritySubjectMap[event::class.java to ignoreCancelled]!![priority]!!.onNext(event)
        } else {
            throw IllegalStateException("Event not listening: ${event::class.qualifiedName}")
        }
    }

    private fun listen(eventClass: Class<out Event>) {
        EventPriority.values().forEach { priority ->
            listOf(true, false).forEach { ignoreCancelled ->
                Bukkit.getPluginManager().registerEvent(eventClass, this, priority,
                        { _, event -> onEvent(event, ignoreCancelled, priority) }, ReactantCore.instance, ignoreCancelled)
            }
        }
    }

    override fun <T : Event> on(componentRegistrant: Any, eventClass: KClass<T>,
                                ignoreCancelled: Boolean, eventPriority: EventPriority): Observable<T> {
        if (!listeningEventClasses.contains(eventClass.java to ignoreCancelled)) {
            listen(eventClass.java)
        }
        @Suppress("UNCHECKED_CAST")
        return (eventPrioritySubjectMap
                .getOrPut(eventClass.java to ignoreCancelled, { HashMap() })
                .getOrPut(eventPriority, { PublishSubject.create() }))
                .doOnError { it.printStackTrace() }
                as Observable<T>
    }

    override fun pushEvent(event: Event) = Bukkit.getPluginManager().callEvent(event)
}
