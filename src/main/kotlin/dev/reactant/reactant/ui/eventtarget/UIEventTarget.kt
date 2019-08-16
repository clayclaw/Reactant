package dev.reactant.reactant.ui.eventtarget

import dev.reactant.reactant.ui.event.UIEvent
import io.reactivex.Observable
import io.reactivex.subjects.Subject

interface UIEventTarget<T : UIEvent> {
    val event: Subject<T>
}

inline fun <T : UIEvent, reified K : T> UIEventTarget<T>.observable(): Observable<K> = event.filter { it is K }.map { it as K }

