package io.reactant.reactant.service.spec.script.kotlin

import io.reactivex.Completable
import io.reactivex.Single
import java.io.File

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

        fun <K : Any> import(path: String): K = importer.import(path)

        fun <K : Any> import(clazz: Class<out Scripting<K>>, path: String): K = importer.import(clazz, path)

        lateinit var export: T
    }

    abstract class ScriptImporter {
        abstract fun <T : Any> import(path: String): T

        abstract fun <K : Any> import(clazz: Class<out Scripting<K>>, path: String): K
    }


}

