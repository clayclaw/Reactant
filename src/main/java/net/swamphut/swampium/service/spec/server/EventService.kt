package net.swamphut.swampium.service.spec.server

import io.reactivex.Observable
import org.bukkit.event.Event
import org.bukkit.event.EventPriority

interface EventService {
    fun <T : Event> on(listener: Any, eventClass: Class<T>,
                       eventPriority: EventPriority = EventPriority.NORMAL): Observable<T>;
}
