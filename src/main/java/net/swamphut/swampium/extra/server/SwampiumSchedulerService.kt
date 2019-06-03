package net.swamphut.swampium.extra.server

import io.reactivex.Completable
import io.reactivex.Observable
import net.swamphut.swampium.core.Swampium
import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.dependency.provide.ServiceProvider
import net.swamphut.swampium.service.spec.server.SchedulerService
import org.bukkit.Bukkit
import org.bukkit.event.Listener


@SwObject
@ServiceProvider([SchedulerService::class])
class SwampiumSchedulerService : Listener, SchedulerService {

    override fun next(): Completable = Completable.defer {
        var taskId: Int? = null
        Completable.create { source ->
            taskId = Bukkit.getScheduler().runTask(Swampium.instance, Runnable { source.onComplete() }).taskId
        }.doOnDispose { Bukkit.getScheduler().cancelTask(taskId!!) }
    }

    override fun timer(delay: Long): Completable = Completable.defer {
        var taskId: Int? = null
        Completable.create { source ->
            taskId = Bukkit.getScheduler().runTaskLater(Swampium.instance, Runnable { source.onComplete() }, delay).taskId
        }.doOnDispose { Bukkit.getScheduler().cancelTask(taskId!!) }
    }

    override fun interval(delay: Long, period: Long): Observable<Int> = Observable.defer {
        var taskId: Int? = null
        var count: Int = 0
        Observable.create<Int> { source ->
            taskId = Bukkit.getScheduler()
                    .runTaskTimer(Swampium.instance, Runnable { source.onNext(count++) }, delay, period).taskId
        }.doOnDispose { Bukkit.getScheduler().cancelTask(taskId!!) }
    }

}
