package io.reactant.reactant.extra.server

import io.reactant.reactant.core.ReactantCore
import io.reactant.reactant.core.component.Component
import io.reactant.reactant.service.spec.server.SchedulerService
import io.reactivex.Completable
import io.reactivex.Completable.defer
import io.reactivex.Observable
import org.bukkit.Bukkit
import org.bukkit.event.Listener


@Component
class ReactantSchedulerService : Listener, SchedulerService {

    override fun next(): Completable = defer {
        var taskId: Int? = null
        Completable.create { source ->
            taskId = Bukkit.getScheduler().runTask(ReactantCore.instance, Runnable { source.onComplete() }).taskId
        }.doOnDispose { Bukkit.getScheduler().cancelTask(taskId!!) }
    }

    override fun timer(delay: Long): Completable = defer {
        var taskId: Int? = null
        Completable.create { source ->
            taskId = Bukkit.getScheduler().runTaskLater(ReactantCore.instance, Runnable { source.onComplete() }, delay).taskId
        }.doOnDispose { Bukkit.getScheduler().cancelTask(taskId!!) }
    }

    override fun interval(delay: Long, period: Long): Observable<Int> = Observable.defer {
        var taskId: Int? = null
        var count: Int = 0
        Observable.create<Int> { source ->
            taskId = Bukkit.getScheduler()
                    .runTaskTimer(ReactantCore.instance, Runnable { source.onNext(count++) }, delay, period).taskId
        }.doOnDispose { Bukkit.getScheduler().cancelTask(taskId!!) }
    }

}
