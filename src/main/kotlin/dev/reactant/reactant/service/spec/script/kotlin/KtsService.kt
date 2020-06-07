package dev.reactant.reactant.service.spec.script.kotlin

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.io.File
import kotlin.reflect.KClass

interface KtsService {
    fun <T : Any> execute(emptyScriptObject: Scripting<T>?, file: File): Single<T>

    fun <T : Any> execute(emptyScriptObject: Scripting<T>, path: String): Single<T>
    fun <T : Any> execute(file: File): Single<T>
    fun <T : Any> execute(path: String): Single<T>
    fun preload(file: File): Completable
    fun reload(file: File): Completable
    fun getImporter(scriptPath: String): ScriptImporter

    /**
     * @param T scripting export type
     */
    abstract class Scripting<T : Any> {
        lateinit var importer: ScriptImporter

        /**
         * Require a reactant component
         */
        inline fun <reified T : Any> require(): T = importer.require(T::class)
        fun <T : Any> import(path: String): T = importer.import(path)
        fun <T : Any> import(clazz: KClass<out Scripting<T>>, path: String): T = importer.import(clazz, path)

        lateinit var export: T
    }

    abstract class ScriptImporter {
        abstract fun <T : Any> require(clazz: KClass<T>): T
        abstract fun <T : Any> import(path: String): T
        abstract fun <T : Any> import(clazz: KClass<out Scripting<T>>, path: String): T
    }


}

