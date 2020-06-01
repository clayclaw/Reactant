package dev.reactant.reactant.extra.profiler

import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.injection.components.Components
import dev.reactant.reactant.service.spec.profiler.ProfilerDataProvider
import dev.reactant.reactant.service.spec.server.SchedulerService
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.withLatestFrom
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

@Component
class ReactantProfilerService(
        private val profilerDataProviders: Components<ProfilerDataProvider>,
        private val schedulerService: SchedulerService
) {
    private val time: BehaviorSubject<Int> = BehaviorSubject.createDefault(0)
            .also { subject -> schedulerService.interval(1).subscribe(subject) }

    private val profilerDataObservable: Observable<Pair<ProfilerDataProvider, ProfilerDataProvider.ProfilerData>>
        get() = Observable.fromIterable(profilerDataProviders)
                .flatMap { dataProvider -> dataProvider.profilerDataObservable.map { dataProvider to it } }

    private var measurerCount = 0
    private val runningMeasurer: HashMap<Int, PublishSubject<Unit>> = HashMap()

    val runningProfilerList get() = runningMeasurer.keys

    /**
     * @return Profiler id - Profiler data Observable pair
     */
    fun startMeasure(): Pair<Int, Observable<Pair<ProfilerDataProvider, ProfilerDataProvider.ProfilerData>>> {
        val controlSubject = PublishSubject.create<Unit>()
        runningMeasurer[measurerCount] = controlSubject
        return (measurerCount++) to profilerDataObservable
                .takeUntil(controlSubject)
                .withLatestFrom(time.hide()) { data, tick: Int ->
                    data.second.tick = tick
                    data
                }
    }

    /**
     * Stop a profiler by profilerId
     */
    fun stopMeasure(profilerId: Int) {
        runningMeasurer[profilerId]!!.apply { onNext(Unit); onComplete() }
        runningMeasurer.remove(profilerId)
    }
}
