package dev.reactant.reactant.extra.script.kotlin

import dev.reactant.reactant.core.ReactantCore
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.instance.ComponentInstanceManager
import dev.reactant.reactant.service.spec.file.text.TextFileReaderService
import dev.reactant.reactant.service.spec.script.kotlin.KtsService
import dev.reactant.reactant.service.spec.script.kotlin.KtsService.ScriptImporter
import dev.reactant.reactant.service.spec.script.kotlin.KtsService.Scripting
import dev.reactant.skill.SimpleScript
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Paths
import java.util.*
import javax.script.ScriptEngineManager
import kotlin.collections.HashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.script.experimental.api.valueOrNull
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromClassloader
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

@Component
class ReactantKtsService(
        private val textFileReader: TextFileReaderService
) : KtsService {

    private val componentInstanceManager: ComponentInstanceManager = ReactantCore.instance.instanceManager

    private val scriptPathFileCache = HashMap<File, (Scripting<out Any>?) -> Scripting<out Any>>()

    //    ReactantCore::class.java.classLoader
//    private val scriptEngineManager = ScriptEngineManager(ReactantCore::class.java.classLoader).getEngineByExtension("kts")!!
    private fun getScriptClassLoader(): URLClassLoader {
        val pluginClassLoader: ClassLoader = ReactantCore::class.java.classLoader

        val bukkitClassLoader: ClassLoader = Thread.currentThread().contextClassLoader
        val fileField = pluginClassLoader::class.java.getDeclaredField("file")
        fileField.isAccessible = true
        val findClassFunc = ClassLoader::class.java.getDeclaredMethod("findClass", String::class.java)
        findClassFunc.isAccessible = true
        val file = fileField.get(pluginClassLoader) as File
        return object : URLClassLoader(arrayOf<URL>(file.toURI().toURL()), bukkitClassLoader) {
            override fun loadClass(p0: String?): Class<*> =
                    kotlin.runCatching { pluginClassLoader.loadClass(p0) }.getOrNull()
                            ?: bukkitClassLoader.loadClass(p0)

            override fun getResource(name: String?): URL? = pluginClassLoader.getResource(name)
            override fun getResources(p0: String?): Enumeration<URL> = pluginClassLoader.getResources(p0)
            override fun findClass(p0: String?): Class<*> = (kotlin.runCatching { findClassFunc.invoke(pluginClassLoader, p0) }
                    .getOrNull() ?: findClassFunc.invoke(bukkitClassLoader, p0)) as Class<*>

        }
    }


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

//    private fun eval(file: File){
//
//    }

    @Suppress("UNCHECKED_CAST")
    override fun reload(file: File): Completable = textFileReader.readAll(file)
            .map {
                ReactantCore.logger.info("Step 0: ${file.path}")
                it.joinToString("\n")
            }
            .map {
                ReactantCore.logger.info("Step 1: ${file.path}")
                val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<SimpleScript> {
                    jvm {
                        dependenciesFromClassloader(classLoader = ReactantCore::class.java.classLoader, wholeClasspath = true)
                    }
                }

                BasicJvmScriptingHost()
                        .eval(file.toScriptSource(), compilationConfiguration, null)
                        .also {
                            it.reports.forEach { it.exception?.let { it.printStackTrace() } }
                        }
                        .valueOrNull()?.returnValue?.scriptInstance?.also { ReactantCore.logger.info(it) }
            }
//            .map {
//                ReactantCore.logger.info(it::class.qualifiedName)
//                val x: (Scripting<out Any>?, ScriptImporter) -> Scripting<out Any> = (it as ScriptCarrier<Scripting<out Any>>).main
//                x
//            }
            .doOnSuccess {
                ReactantCore.logger.info("Step 2: ${file.path}")
            }
//            .doAfterSuccess { callableScript ->
//                ReactantCore.logger.info("Loaded script: ${file.path}")
//                scriptPathFileCache[file] =
//                        { emptyScriptObject -> callableScript(emptyScriptObject, getImporter(file.absolutePath)) }
//            }
            .ignoreElement()


    override fun getImporter(scriptPath: String): ScriptImporter = ScriptImporterImpl(scriptPath)

    inner class ScriptImporterImpl(val originPath: String) : ScriptImporter() {
        override fun <T : Any> require(clazz: KClass<T>): T = componentInstanceManager.getInstance(clazz)!!

        override fun <T : Any> import(path: String): T = execute<T>(
                if (Paths.get(path).isAbsolute) Paths.get(path).toString()
                else Paths.get(originPath, path).normalize().toString()
        ).blockingGet()

        override fun <K : Any> import(clazz: KClass<out Scripting<K>>, path: String): K = import(path)
    }


    companion object {
        fun <T : Scripting<out Any>> createScriptingObject(scriptingObjectClass: KClass<T>, scriptImporter: ScriptImporter): T {
            val primaryConstructor = scriptingObjectClass.primaryConstructor
            if (primaryConstructor == null || !primaryConstructor.parameters.isEmpty())
                throw IllegalArgumentException("A parameterless primary constructor of scripting class is required " +
                        "if calling script without providing an empty script object")
            val scriptingObject = primaryConstructor.call()
            scriptingObject.importer = scriptImporter
            return scriptingObject
        }
    }
}

inline fun <reified T : Scripting<out Any>> scripting(crossinline content: T.() -> Unit)
        : (T?, ScriptImporter) -> T {
    return { emptyScriptObj: T?, scriptImporter: ScriptImporter ->
        (emptyScriptObj ?: ReactantKtsService.createScriptingObject(T::class, scriptImporter)).apply(content)
    }
}

