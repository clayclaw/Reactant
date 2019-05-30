package net.swamphut.swampium.repository.commands.repository

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import net.swamphut.swampium.core.Swampium
import net.swamphut.swampium.extra.command.SwCommand
import net.swamphut.swampium.repository.MavenRepositoryRetrieverService
import net.swamphut.swampium.repository.RepositoryService
import net.swamphut.swampium.repository.commands.RepositoryPermission
import net.swamphut.swampium.utils.converter.StacktraceConverterUtils
import okio.Okio
import org.bukkit.Bukkit
import org.bukkit.plugin.InvalidDescriptionException
import org.bukkit.plugin.PluginDescriptionFile
import picocli.CommandLine
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URLClassLoader
import java.util.*

@CommandLine.Command(name = "retrieve", aliases = ["ret"], mixinStandardHelpOptions = true,
        description = ["Retrieve a plugin from maven repositories"])
class RepositoryRetrieveSubCommand(private val repositoryService: RepositoryService,
                                   private val repositoryRetrieverService: MavenRepositoryRetrieverService) : SwCommand() {
    @CommandLine.Parameters(arity = "1..*", paramLabel = "IDENTIFIER",
            description = ["Gradle style identifier of the plugins artifact",
                "format: \"{groupId}:{artifactId}:{version}:{classifier(optional)}\""])
    lateinit var identifiers: ArrayList<String>

    @CommandLine.Option(names = ["-v", "--verbose"],
            description = ["Verbose mode, print more details"])
    var verbose: Boolean = false

    override fun run() {
        requirePermission(RepositoryPermission.Companion.SWAMPIUM.REPOSITORY.RETRIEVE)
        Observable.fromIterable(identifiers)
                .flatMapSingle(this::download)
                .flatMapSingle(this::replaceCurrentPlugin)
                .subscribe({ stdout.out("Retrieve successfully as ${it.absolutePath}") },
                        {
                            stderr.out("Retrieve failed :${it.message ?: ""}");
                            if (verbose)
                                StacktraceConverterUtils.convertToString(it).split("\n").forEach(stdout::out)
                        })
    }

    @Suppress("UNCHECKED_CAST")
    private fun download(identifier: String): Single<File> {
        val extension = if (identifier.contains("@")) identifier.split("@").last() else "jar"
        val splited = identifier.split("@").first().split(":")
        if (splited.size < 3) return Single.error(RetrieveException(identifier, "Identifier format incorrect"))
        val groupId = splited[0]
        val artifactId = splited[1]
        val version = splited[2]
        val classifier = if (splited.size > 3) splited[3] else null

        val randomId = UUID.randomUUID()

        val targetFile =
                if (classifier == null) File(Swampium.tmpDirPath + "/$randomId/$artifactId-$version.$extension")
                else File(Swampium.tmpDirPath + "/$randomId/$artifactId-$version-$classifier.$extension")
        targetFile.parentFile.mkdirs()

        return repositoryService.repositoriesMap.values.map { repo ->
            if (classifier == null) repositoryRetrieverService.getArtifact(repo, groupId, artifactId, version, extension)
            else repositoryRetrieverService.getArtifact(repo, groupId, artifactId, version, extension, classifier)
        }.let { requests ->
            Observable.fromIterable(requests).flatMapSingle {
                it.doOnSuccess { responseBody ->
                    targetFile.createNewFile()
                    Okio.buffer(Okio.sink(targetFile)).apply {
                        writeAll(responseBody.source())
                        close()
                    }
                }.map { true }.onErrorReturn { false }

            }.takeUntil { it }.toList().flatMap { failedRequest ->
                if (!targetFile.exists()) Single.error { Exception("Artifact not found") }
                else Single.just(targetFile)
            }
        }.subscribeOn(Schedulers.io())
    }

    private fun replaceCurrentPlugin(newPluginFile: File): Single<File> = Single.fromCallable {
        val classLoader = URLClassLoader(arrayOf(newPluginFile.toURI().toURL()))
        val pluginDescription: PluginDescriptionFile
        try {
            pluginDescription = PluginDescriptionFile(classLoader.getResourceAsStream("plugin.yml"))
        } catch (e: InvalidDescriptionException) {
            throw IllegalStateException("Not a valid plugin");
        }
        val newLocation = File("plugins/${newPluginFile.name}");


        val currentPlugin = Bukkit.getPluginManager().plugins
                .filter { it.description.main == pluginDescription.main }
                .first()
        val oldName = currentPlugin.description.fullName
        currentPlugin?.let {
            Bukkit.getPluginManager().disablePlugin(it)
            stdout.out("Updating: $oldName->${pluginDescription.fullName}")
            File(it::class.java.protectionDomain.codeSource.location.toURI()).apply {
                if (absolutePath != newLocation.absolutePath)
                    stdout.out("You must delete ${absolutePath} manually after update")
            }
        }


        val input = FileInputStream(newPluginFile).channel
        val output = FileOutputStream(newLocation).channel
        try {
            input.transferTo(0, input.size(), output)
        } finally {
            input.close();
            output.close()
        }

        Bukkit.getPluginManager().loadPlugin(newLocation).let { Bukkit.getPluginManager().enablePlugin(it!!) }
        newPluginFile
    }.subscribeOn(Swampium.mainThreadScheduler)

    class RetrieveException(val identifier: String, val reason: String) : Exception("Failed to retrieve $identifier, $reason") {

    }
}