package net.swamphut.swampium.ui.eventtarget

import io.reactivex.Observable
import io.reactivex.subjects.Subject
import net.swamphut.swampium.ui.event.UIEvent

interface UIEventTarget<T : UIEvent> {
    val event: Subject<T>
}

inline fun <T : UIEvent, reified K : T> UIEventTarget<T>.observable(): Observable<K> = event.filter { it is K }.map { it as K }

