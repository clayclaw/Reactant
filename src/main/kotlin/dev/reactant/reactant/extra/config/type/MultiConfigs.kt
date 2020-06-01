package dev.reactant.reactant.extra.config.type

import dev.reactant.reactant.service.spec.config.Config
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.io.File
import kotlin.reflect.KClass

interface MultiConfigs<T : Any> {
    val modelClass: KClass<T>
    val configFolder: File

    fun get(relativePath: String): Maybe<Config<T>>
    fun getOrDefault(relativePath: String, defaultContentCallable: () -> T): Single<Config<T>>
    fun getOrPut(relativePath: String, defaultContentCallable: () -> T): Single<Config<T>>

    fun getAll(recursively: Boolean = true): Observable<Config<T>>
    fun getAllAsMap(recursively: Boolean = true): Single<Map<String, Config<T>>> =
            getAll(recursively).collect(
                    { HashMap<String, Config<T>>() },
                    { configSet, next -> configSet[next.path.removePrefix("${configFolder.absolutePath}/")] = next }
            ).map { it.toMap() }

}
