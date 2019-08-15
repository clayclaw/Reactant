package io.reactant.reactant.extra.server

import io.reactant.reactant.core.ReactantCore
import io.reactant.reactant.core.component.Component
import io.reactant.reactant.core.component.lifecycle.LifeCycleHook
import io.reactant.reactant.service.spec.server.EventService
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
    private val eventPrioritySubjectMap = HashMap<Class<out Event>, HashMap<EventPriority, PublishSubject<Event>>>();
    private val listeningEventClasses = HashSet<Class<out Event>>();

    override fun onDisable() {
        HandlerList.unregisterAll(this)
        eventPrioritySubjectMap.flatMap { it.value.map { it.value } }.map { it.onComplete() }
    }

    private fun onEvent(event: Event, priority: EventPriority) {
        if (eventPrioritySubjectMap.containsKey(event::class.java)
                && eventPrioritySubjectMap[event::class.java]!!.containsKey(priority)) {
            eventPrioritySubjectMap
                    .getOrPut(event::class.java, { HashMap() })
                    .getOrPut(priority, { PublishSubject.create<Event>() }).onNext(event)
        }
    }

    private fun listen(eventClass: Class<out Event>) {
        EventPriority.values().forEach { priority ->
            Bukkit.getPluginManager().registerEvent(eventClass, this, priority, { _, event -> onEvent(event, priority) }, ReactantCore.instance)
        }
    }

    override fun <T : Event> on(componentRegistrant: Any, eventClass: KClass<T>, eventPriority: EventPriority): Observable<T> {
        if (!listeningEventClasses.contains(eventClass.java)) {
            listen(eventClass.java)
        }
        @Suppress("UNCHECKED_CAST")
        return (eventPrioritySubjectMap
                .getOrPut(eventClass.java, { HashMap() })
                .getOrPut(eventPriority, { PublishSubject.create<Event>() }))
                as Observable<T>
    }
}
