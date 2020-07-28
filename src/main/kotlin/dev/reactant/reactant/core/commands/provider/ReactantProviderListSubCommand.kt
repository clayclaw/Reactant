package dev.reactant.reactant.core.commands.provider

import dev.reactant.reactant.core.commands.ReactantPermissions
import dev.reactant.reactant.core.component.container.ContainerManager
import dev.reactant.reactant.core.dependency.ProviderManager
import dev.reactant.reactant.core.dependency.injection.producer.Provider
import dev.reactant.reactant.extra.command.ReactantCommand
import dev.reactant.reactant.utils.PatternMatchingUtils
import dev.reactant.reactant.utils.formatting.MultiColumns
import picocli.CommandLine
import java.util.regex.Pattern
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.jvmName

@CommandLine.Command(
        name = "f",
        aliases = ["find"],
        mixinStandardHelpOptions = true,
        description = ["Find available Provider"]
)
internal class ReactantProviderListSubCommand(
        val providerManager: ProviderManager,
        val containerManager: ContainerManager
) : ReactantCommand() {

    @CommandLine.Option(names = ["-p", "--pattern"], paramLabel = "REG_EXP",
            description = ["Filtering Provider name by RegExp"])
    var namePattern: Pattern? = null

    @CommandLine.Option(names = ["-o", "--short"],
            description = ["Display class short name instead of canonical name"])
    var showShortName: Boolean = false

    @CommandLine.Option(names = ["-c", "--container"],
            description = ["Filtering the Component by container rawIdentifier, wildcard is available"])
    val containerIdentifierWildcards: ArrayList<String> = arrayListOf()

    @CommandLine.Option(names = ["-n", "--name"],
            description = ["Filtering Provider name, wildcard is available"])
    var nameWildcards: ArrayList<String> = arrayListOf();


    @CommandLine.Parameters(arity = "1", paramLabel = "TARGET_CLASS_NAME",
            description = ["The canonical name of searching class or interface"])
    var targetClassName: String = ""

    private val listTable = MultiColumns.create {
        column { align = MultiColumns.Alignment.Right }
        column { maxLength = 50; overflowCutFromRight = false; }
        column { maxLength = 40 }
        column { align = MultiColumns.Alignment.Center }
    }

    override fun execute() {
        requirePermission(ReactantPermissions.ADMIN.DEV.OBJ.LIST)

        val targetClass = kotlin.runCatching { Class.forName(targetClassName) }.getOrElse {
            stderr.out("Class not found $targetClassName")
            return
        }

        providerManager.providers.union(providerManager.blacklistedProviders)
                .filter { it.canProvideType(targetClass.kotlin.starProjectedType) }
                .asSequence()
                // State filter
                .filter {
                    namePattern == null
                            || namePattern!!.toRegex().matches(it.productType.jvmErasure.jvmName)
                }
                // Class name wildcards filter
                .filter { matchNameWildcards(it) }
                .filter { matchContainerIdentifierWildcards(it.container.identifier) }
                .toList()
                .forEach { addToListTable(it) }
        listTable.generate().forEach(stdout::out)
    }

    private fun matchNameWildcards(provider: Provider) =
            nameWildcards.isEmpty() || nameWildcards.any { wildcard ->
                PatternMatchingUtils.matchWildcard(wildcard, provider.productType.jvmErasure.jvmName)
            }

    private fun matchContainerIdentifierWildcards(identifier: String) =
            containerIdentifierWildcards.isEmpty() || containerIdentifierWildcards.any { wildcard ->
                PatternMatchingUtils.matchWildcard(wildcard, identifier)
            }

    private fun addToListTable(provider: Provider) {
        listTable.rows.add(listOf(
                provider.hashCode().toString(36),
                provider.productType.jvmErasure.let { if (showShortName) it.simpleName!! else it.jvmName },
                provider.container.identifier,
                if (providerManager.blacklistedProviders.contains(provider)) "Blacklisted"
                else ""
        ))
    }

}
