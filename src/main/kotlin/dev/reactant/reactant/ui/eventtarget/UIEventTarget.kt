package dev.reactant.reactant.ui.eventtarget

import dev.reactant.reactant.ui.event.UIEvent
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.Subject

interface UIEventTarget<T : UIEvent> {
    val event: Subject<T>
}

inline fun <T : UIEvent, reified K : T> UIEventTarget<T>.observable(): Observable<K> = event.filter { it is K }.map { it as K }

