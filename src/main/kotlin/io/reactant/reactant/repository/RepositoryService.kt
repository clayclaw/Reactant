package io.reactant.reactant.repository

import io.reactant.reactant.core.ReactantCore.Companion.configDirPath
import io.reactant.reactant.core.component.Component
import io.reactant.reactant.core.dependency.injection.Inject
import io.reactant.reactant.extra.command.exceptions.CommandExecutionPermissionException
import io.reactant.reactant.repository.config.RepositoryConfig
import io.reactant.reactant.service.spec.config.Config
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
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
                    .observeOn(io.reactant.reactant.core.ReactantCore.mainThreadScheduler)
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
