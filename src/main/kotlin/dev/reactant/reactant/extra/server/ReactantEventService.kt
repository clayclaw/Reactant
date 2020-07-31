package dev.reactant.reactant.extra.server

import PublishingProfilerDataProvider
import dev.reactant.reactant.core.ReactantCore
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import dev.reactant.reactant.core.dependency.injection.Provide
import dev.reactant.reactant.core.dependency.injection.producer.Provider
import dev.reactant.reactant.core.dependency.layers.SystemLevel
import dev.reactant.reactant.service.spec.profiler.ProfilerDataProvider
import dev.reactant.reactant.service.spec.server.EventService
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import kotlin.reflect.KClass
import kotlin.reflect.KType


@Component
private class ReactantEventServiceProvider(
        private val profilerDataProvider: PublishingProfilerDataProvider = PublishingProfilerDataProvider()
) : LifeCycleHook, Listener, ProfilerDataProvider by profilerDataProvider, SystemLevel {
    /**
     * Map which using Pair<EventClass, Boolean> as key, while the Boolean is ignoreCancelled
     */
    private val eventPrioritySubjectMap = HashMap<Pair<Class<out Event>, Boolean>, HashMap<EventPriority, PublishSubject<Event>>>();
    private val listeningEventClasses = HashSet<Class<out Event>>();

    override fun onDisable() {
        HandlerList.unregisterAll(this)
        eventPrioritySubjectMap.flatMap { it.value.map { it.value } }.map { it.onComplete() }
    }

    @Provide(".*", true)
    private fun getEventService(kType: KType, path: String, requester: Provider) = ReactantEventService(requester)

    private inner class ReactantEventService(override val requester: Provider) : EventService {

        private fun onEvent(event: Event, ignoreCancelled: Boolean, priority: EventPriority) {
            if (eventPrioritySubjectMap.containsKey(event::class.java to ignoreCancelled)
                    && eventPrioritySubjectMap[event::class.java to ignoreCancelled]!!.containsKey(priority)) {
                eventPrioritySubjectMap[event::class.java to ignoreCancelled]!![priority]!!.onNext(event)
            }
        }

        /**
         * Register the event listener if the event class has never be reigstered
         */
        private fun listen(eventClass: Class<out Event>) {
            if (!listeningEventClasses.contains(eventClass)) {
                listeningEventClasses.add(eventClass)
                EventPriority.values().forEach { priority ->
                    listOf(true, false).forEach { ignoreCancelled ->
                        Bukkit.getPluginManager().registerEvent(eventClass, this@ReactantEventServiceProvider, priority,
                                { _, event -> onEvent(event, ignoreCancelled, priority) }, ReactantCore.instance, ignoreCancelled)
                    }
                }
            }
        }

        override fun <T : Event> on(eventClass: KClass<T>, ignoreCancelled: Boolean, eventPriority: EventPriority): Observable<T> {
            listen(eventClass.java)

            var disposable: Disposable? = null

            return Observable.create<T> { source ->
                disposable = (eventPrioritySubjectMap
                        .getOrPut(eventClass.java to ignoreCancelled, { HashMap() })
                        .getOrPut(eventPriority, { PublishSubject.create() }))
                        .doOnError { it.printStackTrace() }
                        .subscribe { event ->
                            profilerDataProvider.measure(listOf(eventClass.qualifiedName ?: "Unknown"), requester) {
                                source.onNext(event as T)
                            }
                        }
            }.doOnDispose { disposable!!.dispose() }
        }

        override fun pushEvent(event: Event) = Bukkit.getPluginManager().callEvent(event)
    }
}
