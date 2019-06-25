package net.swamphut.swampium.repository

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import net.swamphut.swampium.core.Swampium
import net.swamphut.swampium.core.dependency.injection.Inject
import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleHook
import net.swamphut.swampium.extra.command.exceptions.CommandExecutionPermissionException
import net.swamphut.swampium.repository.config.RepositoryConfig
import net.swamphut.swampium.service.spec.config.Config
import net.swamphut.swampium.service.spec.config.ConfigService
import net.swamphut.swampium.service.spec.config.loadOrDefault
import net.swamphut.swampium.service.spec.parser.JsonParserService
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import java.net.URL

@SwObject
class RepositoryService(
        @Inject(Swampium.configDirPath + "/repository.json") private val repositoryConfig: Config<RepositoryConfig>
) {

    fun getRepository(name: String): String? {
        return repositoryConfig.content.repositories[name]
    }

    val repositoriesMap get() = repositoryConfig.content.repositories.toMap()

    fun setRepository(name: String, url: String, connectionChecking: Boolean = true): Completable =
            Completable.fromAction { if (connectionChecking) URL(url.trimEnd('/')).openConnection().connect() }
                    .subscribeOn(Schedulers.io())
                    .observeOn(Swampium.mainThreadScheduler)
                    .andThen(Completable.fromAction { repositoryConfig.content.repositories[name] = url.trimEnd('/') })
                    .observeOn(Schedulers.io())
                    .andThen(repositoryConfig.save())

    fun removeRepository(name: String): Completable {
        if (!repositoryConfig.content.repositories.containsKey(name))
            throw IllegalArgumentException("$name not an existing repository")
        repositoryConfig.content.repositories.remove(name)
        return Completable.fromCallable { repositoryConfig.save() }
                .subscribeOn(Schedulers.io())
    }

    /**
     * Based on the config decide allow player to execute or not
     */
    fun consoleOnlyValidate(sender: CommandSender) {
        if (repositoryConfig.content.consoleOnly && sender !is ConsoleCommandSender)
            throw CommandExecutionPermissionException(sender, "CONSOLE", "Repository action")
    }
}
