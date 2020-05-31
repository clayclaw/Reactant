package dev.reactant.reactant.service.spec.server

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler

interface SchedulerService {
    fun next(): Completable
    fun timer(delay: Long): Completable
    fun interval(delay: Long, period: Long): Observable<Int>
    fun interval(period: Long): Observable<Int> = interval(0, period)
    val mainThreadScheduler: Scheduler
}
