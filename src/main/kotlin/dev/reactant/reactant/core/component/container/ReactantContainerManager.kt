package dev.reactant.reactant.core.component.container

import com.google.gson.Gson
import dev.reactant.reactant.core.ReactantCore
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.configs.ServiceMatchingConfig
import dev.reactant.reactant.core.dependency.ProviderManager
import dev.reactant.reactant.core.dependency.injection.producer.ComponentProvider
import dev.reactant.reactant.core.dependency.injection.producer.DynamicProvider
import dev.reactant.reactant.core.dependency.injection.producer.DynamicSubtypeProvider
import dev.reactant.reactant.core.dependency.injection.producer.Provider
import dev.reactant.reactant.utils.PatternMatchingUtils
import java.io.File
import java.io.FileReader
import kotlin.reflect.jvm.jvmName

@Component
class ReactantContainerManager : ContainerManager {
    private var matchingConfig: ServiceMatchingConfig = File("${ReactantCore.configDirPath}/services.json")
        .let { file ->
            if (file.exists()) FileReader(file).use { reader ->
                Gson().fromJson(
                    reader,
                    ServiceMatchingConfig::class.java
                )
            }
            else ServiceMatchingConfig()
        }

    private val componentContainerMap = HashMap<String, Container>()

    private val instanceManager = ReactantCore.instance.instanceManager
    private val dependencyManager = instanceManager.getInstance(ProviderManager::class)!!

    override val containers: Collection<Container> get() = componentContainerMap.values
    private val containerInjectableProviderMap = HashMap<Container, HashSet<Provider>>()

    override fun getContainer(identifier: String): Container? {
        return componentContainerMap[identifier]
    }

    override fun addContainer(container: Container) {
        require(componentContainerMap[container.identifier] == null) { "Component Container with same rawIdentifier already exist: ${container.identifier}" }
        componentContainerMap[container.identifier] = container
        addAllInjectableProvider(container)
    }

    private fun addAllInjectableProvider(container: Container) {
        container.componentClasses
            .filter { it.java.isAnnotationPresent(Component::class.java) }
            .map { ComponentProvider.fromComponent(it, instanceManager, container) }.forEach {
                // providers including component and @Provide
                listOf(it).union(DynamicProvider.findAllFromComponentInjectableProvider(it))
                    .union(DynamicSubtypeProvider.findAllFromComponentInjectableProvider(it))
                    .onEach { provider ->
                        containerInjectableProviderMap.getOrPut(container) { hashSetOf() }.add(provider)
                    }
                    .forEach { provider ->
                        val providerName = it.componentClass.jvmName
                        val isBlacklisted = matchingConfig.blacklistServicePatterns
                            .any { pattern -> PatternMatchingUtils.matchWildcardOrRegex(pattern, providerName) }
                        if (isBlacklisted) dependencyManager.addBlacklistedProvider(provider)
                        else dependencyManager.addProvider(provider)
                    }
            }
    }

    override fun removeContainer(container: Container) {
        requireNotNull(componentContainerMap[container.identifier]) { "Component Container not exist: ${container.identifier}" }

        containerInjectableProviderMap[container]!!.forEach { dependencyManager.removeProvider(it) }
        containerInjectableProviderMap.remove(container)
        componentContainerMap.remove(container.identifier)
    }

    override fun getContainerProvidedInjectableProvider(container: Container): Set<Provider> =
        containerInjectableProviderMap[container] ?: setOf()
}
