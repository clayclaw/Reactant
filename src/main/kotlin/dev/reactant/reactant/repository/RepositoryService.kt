package dev.reactant.reactant.repository

import dev.reactant.reactant.core.ReactantCore.Companion.configDirPath
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.injection.Inject
import dev.reactant.reactant.extra.command.exceptions.CommandExecutionPermissionException
import dev.reactant.reactant.repository.config.RepositoryConfig
import dev.reactant.reactant.service.spec.config.Config
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import java.net.URL

@Component
class RepositoryService(
        @Inject(configDirPath + "/repository.json") private val repositoryConfig: Config<RepositoryConfig>
) {

    fun getRepository(name: String): String? {
        return repositoryConfig.content.repositories[name]
    }

    val repositoriesMap get() = repositoryConfig.content.repositories.toMap()

    fun setRepository(name: String, url: String, connectionChecking: Boolean = true): Completable =
            Completable.fromAction { if (connectionChecking) URL(url.trimEnd('/')).openConnection().connect() }
                    .subscribeOn(Schedulers.io())
                    .observeOn(dev.reactant.reactant.core.ReactantCore.mainThreadScheduler)
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
