package io.reactant.reactant.extra.server

import io.reactant.reactant.core.ReactantCore
import io.reactant.reactant.core.reactantobj.container.Reactant
import io.reactant.reactant.core.reactantobj.lifecycle.LifeCycleHook
import io.reactant.reactant.service.spec.server.EventService
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.RegisteredListener
import kotlin.reflect.KClass


@Reactant
class ReactantEventService : LifeCycleHook, Listener, EventService {
    private val eventPrioritySubjectMap = HashMap<Class<out Event>, HashMap<EventPriority, PublishSubject<Event>>>();
    private val listeners: HashSet<RegisteredListener> = hashSetOf()

    override fun onEnable() {
        EventPriority.values().forEach { priority ->
            val listener = RegisteredListener(this, { _, event -> onEvent(event, priority) },
                    priority, ReactantCore.instance, false);
            HandlerList.getHandlerLists().forEach { it.register(listener) }
            listeners.add(listener)
        }
    }

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

    override fun <T : Event> on(registerReactantObject: Any, eventClass: KClass<T>, eventPriority: EventPriority): Observable<T> {
        @Suppress("UNCHECKED_CAST")
        return (eventPrioritySubjectMap
                .getOrPut(eventClass.java, { HashMap() })
                .getOrPut(eventPriority, { PublishSubject.create<Event>() }))
                as Observable<T>
    }
}
