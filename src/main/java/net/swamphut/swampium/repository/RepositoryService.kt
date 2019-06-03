package net.swamphut.swampium.repository

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import net.swamphut.swampium.core.Swampium
import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.dependency.provide.ServiceProvider
import net.swamphut.swampium.core.swobject.dependency.injection.Inject
import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleHook
import net.swamphut.swampium.repository.config.RepositoryConfig
import net.swamphut.swampium.service.spec.config.Config
import net.swamphut.swampium.service.spec.config.ConfigService
import net.swamphut.swampium.service.spec.config.loadOrDefault
import net.swamphut.swampium.service.spec.parser.JsonParserService
import java.net.URL

@SwObject
@ServiceProvider
class RepositoryService : LifeCycleHook {
    @Inject
    private lateinit var jsonParser: JsonParserService
    @Inject
    private lateinit var configService: ConfigService

    private lateinit var repositoryConfig: Config<RepositoryConfig>

    override fun init() {
        repositoryConfig = configService.loadOrDefault(jsonParser, Swampium.configDirPath + "/repository.json", ::RepositoryConfig)
                .blockingGet().also { it.save().blockingAwait() }
    }

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
}
