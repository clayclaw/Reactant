package io.reactant.reactant.core.commands.component

import io.reactant.reactant.core.commands.ReactantPermissions.Companion.Reactant
import io.reactant.reactant.core.component.container.Container
import io.reactant.reactant.core.component.container.ContainerManager
import io.reactant.reactant.core.dependency.ProviderManager
import io.reactant.reactant.core.dependency.injection.producer.ComponentProvider
import io.reactant.reactant.extra.command.ReactantCommand
import io.reactant.reactant.utils.PatternMatchingUtils
import io.reactant.reactant.utils.formatting.MultiColumns
import picocli.CommandLine
import java.util.regex.Pattern
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.jvmName

@CommandLine.Command(
        name = "ls",
        aliases = ["list"],
        mixinStandardHelpOptions = true,
        description = ["Listing all Component"]
)
class ComponentListSubCommand(
        val providerManager: ProviderManager,
        val containerManager: ContainerManager
) : ReactantCommand() {

    @CommandLine.Option(names = ["-r", "--is-running"], paramLabel = "IS_RUNNING",
            description = ["Filtering Component by running state"])
    var isRunning: Boolean? = null

    @CommandLine.Option(names = ["-p", "--pattern"], paramLabel = "REG_EXP",
            description = ["Filtering Component class canonical name by RegExp"])
    var classNamePattern: Pattern? = null

    @CommandLine.Option(names = ["-o", "--short"],
            description = ["Display class short name instead of canonical name"])
    var showShortName: Boolean = false

    @CommandLine.Option(names = ["-c", "--container"],
            description = ["Filtering the Component by container rawIdentifier, wildcard is available"])
    val containerIdentifierWildcards: ArrayList<String> = arrayListOf()

    @CommandLine.Parameters(arity = "0..*", paramLabel = "CLASS_NAME",
            description = ["Filtering Component class canonical name, wildcard is available"])
    var classNameWildcards: ArrayList<String> = arrayListOf();

    private val listTable = MultiColumns.create {
        column { align = MultiColumns.Alignment.Right }
        column { maxLength = 50; overflowCutFromRight = false; }
        column { maxLength = 40 }
        column { align = MultiColumns.Alignment.Center }
    }

    override fun run() {
        requirePermission(Reactant.REACTANT_OBJ.LIST)

        providerManager.providers.mapNotNull { it as? ComponentProvider<*> }
                .asSequence()
                // State filter
                .filter { isRunning == null || it.isInitialized() == isRunning }
                // Class name pattern filter
                .filter {
                    classNamePattern == null
                            || classNamePattern!!.toRegex().matches(it.productType.jvmErasure.jvmName)
                }
                // Class name wildcards filter
                .filter { matchClassNameWildcards(it) }
                // wrap it with its container
                .map { Pair(it, containerManager.containers.first { container -> it.productType.jvmErasure in container.componentClasses }) }
                // Container rawIdentifier filter
                .filter { matchContainerIdentifierWildcards(it.second.identifier) }
                .toList()
                .forEach { addToListTable(it) }
        listTable.generate().forEach(stdout::out)
    }

    private fun matchClassNameWildcards(componentWrapper: ComponentProvider<*>) =
            classNameWildcards.isEmpty() || classNameWildcards.any { wildcard ->
                PatternMatchingUtils.matchWildcard(wildcard, componentWrapper.productType.jvmErasure.jvmName)
            }

    private fun matchContainerIdentifierWildcards(identifier: String) =
            containerIdentifierWildcards.isEmpty() || containerIdentifierWildcards.any { wildcard ->
                PatternMatchingUtils.matchWildcard(wildcard, identifier)
            }

    private fun addToListTable(componentWrapperContainerPair: Pair<ComponentProvider<*>, Container>) {
        val componentWrapper = componentWrapperContainerPair.first;
        val container = componentWrapperContainerPair.second;
        listTable.rows.add(listOf<String>(
                componentWrapper.hashCode().toString(36),
                componentWrapper.productType.jvmErasure.let { if (showShortName) it.simpleName!! else it.jvmName },
                container.identifier,
                if (componentWrapper.isInitialized()) "Running"
                else if (componentWrapper.catchedThrowable != null) "Error"
                else if (!componentWrapper.fulfilled) "Not Fulfilled" else ""
        ))
    }

}
