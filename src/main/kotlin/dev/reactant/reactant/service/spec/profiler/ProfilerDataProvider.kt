package dev.reactant.reactant.service.spec.profiler

import io.reactivex.Observable

interface ProfilerDataProvider {
    val profilerDataObservable: Observable<ProfilerData>

    /**
     * Represent a record of a snippet running time
     * @param path The path that represents the structure
     * @param time The running time in nanosecond
     */
    data class ProfilerData(
            val path: List<String>,
            val target: String,
            val time: Long,
            var tick: Int = 0
    )
}
