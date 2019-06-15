package net.swamphut.swampium.extra.script.kotlin

import io.reactivex.Completable
import io.reactivex.Single
import net.swamphut.swampium.core.Swampium
import net.swamphut.swampium.core.dependency.injection.Inject
import net.swamphut.swampium.extra.file.SwampiumTextFileReaderService
import net.swamphut.swampium.service.spec.script.kotlin.KtsService
import net.swamphut.swampium.service.spec.script.kotlin.KtsService.ScriptImporter
import net.swamphut.swampium.service.spec.script.kotlin.KtsService.Scripting
import java.io.File
import java.nio.file.Paths
import javax.script.ScriptEngineManager
import kotlin.reflect.full.primaryConstructor

//@SwObject
@Deprecated("Kts Jsr223 Implementation cannot resolve class by class loader currently")
class SwampiumKtsService : KtsService {

    @Inject
    private lateinit var textFileReaderService: SwampiumTextFileReaderService

    private val scriptPathFileCache = HashMap<File, (Scripting<out Any>?) -> Scripting<out Any>>()

    private val scriptEngineManager = ScriptEngineManager(Swampium::class.java.classLoader)
            .getEngineByExtension("kts")


    override fun <T : Any> execute(emptyScriptObject: Scripting<T>, path: String): Single<T> =
            execute(emptyScriptObject, File(path))

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> execute(emptyScriptObject: Scripting<T>?, file: File): Single<T> =
            preload(file)
                    .toSingle { scriptPathFileCache[file]!! }
                    .map { it(emptyScriptObject) }
                    .map { it.export as T }

    override fun <T : Any> execute(path: String): Single<T> =
            execute(File(path))


    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> execute(file: File): Single<T> =
            preload(file)
                    .toSingle { scriptPathFileCache[file]!! }
                    .map { it(null) }
                    .map { it.export as T }

    override fun preload(file: File): Completable =
            if (!scriptPathFileCache.containsKey(file)) reload(file) else Completable.complete()

    @Suppress("UNCHECKED_CAST")
    override fun reload(file: File): Completable = textFileReaderService.readAll(file)
            .map { it.joinToString("\n") }
            .map {
                scriptEngineManager.eval(it)
            }
            .map { it as (Scripting<out Any>?, ScriptImporter) -> Scripting<out Any> }
            .doAfterSuccess { callableScript ->
                scriptPathFileCache[file] =
                        { emptyScriptObject -> callableScript(emptyScriptObject, getImporter(file.absolutePath)) }
            }
            .ignoreElement()


    override fun getImporter(scriptPath: String): ScriptImporter = ScriptImporterImpl(scriptPath)

    inner class ScriptImporterImpl(val originPath: String) : ScriptImporter() {
        override fun <T : Any> import(path: String): T = execute<T>(
                if (Paths.get(path).isAbsolute) Paths.get(path).toString()
                else Paths.get(originPath, path).normalize().toString()
        ).blockingGet()

        override fun <K : Any> import(clazz: Class<out Scripting<K>>, path: String): K = import(path)
    }
}

inline fun <reified T : Scripting<out Any>> scripting(crossinline block: T.() -> Unit)
        : (T?, ScriptImporter) -> T {
    return { emptyScriptObj: T?, scriptImporter: ScriptImporter ->

        val targetScriptObject = if (emptyScriptObj == null) {
            val primaryConstructor = T::class.primaryConstructor
            if (primaryConstructor == null || !primaryConstructor.parameters.isEmpty())
                throw IllegalArgumentException("A parameterless primary constructor of scripting class is required " +
                        "if calling script without providing an empty script object")
            primaryConstructor.call()
        } else {
            emptyScriptObj
        }

        targetScriptObject.apply {
            importer = scriptImporter;
            block()
        }
    }
}

