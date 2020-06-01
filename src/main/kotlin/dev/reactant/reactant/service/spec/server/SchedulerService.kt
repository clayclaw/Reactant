package dev.reactant.reactant.service.spec.server

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler

interface SchedulerService {
    fun next(): Completable
    fun timer(delay: Long): Completable
    fun interval(delay: Long, period: Long): Observable<Int>
    fun interval(period: Long): Observable<Int> = interval(0, period)
    val mainThreadScheduler: Scheduler
}
