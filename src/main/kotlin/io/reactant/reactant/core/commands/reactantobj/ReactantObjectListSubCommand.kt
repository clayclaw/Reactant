package io.reactant.reactant.core.commands.reactantobj

import io.reactant.reactant.core.commands.ReactantPermissions.Companion.Reactant
import io.reactant.reactant.core.dependency.DependencyManager
import io.reactant.reactant.core.dependency.injection.producer.ReactantObjectInjectableWrapper
import io.reactant.reactant.core.reactantobj.container.Container
import io.reactant.reactant.core.reactantobj.container.ContainerManager
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
        description = ["Listing all ReactantObject"]
)
class ReactantObjectListSubCommand(
        val dependencyManager: DependencyManager,
        val containerManager: ContainerManager
) : ReactantCommand() {

    @CommandLine.Option(names = ["-r", "--is-running"], paramLabel = "IS_RUNNING",
            description = ["Filtering ReactantObject by running state"])
    var isRunning: Boolean? = null

    @CommandLine.Option(names = ["-p", "--pattern"], paramLabel = "REG_EXP",
            description = ["Filtering ReactantObject class canonical name by RegExp"])
    var classNamePattern: Pattern? = null

    @CommandLine.Option(names = ["-o", "--short"],
            description = ["Display class short name instead of canonical name"])
    var showShortName: Boolean = false

    @CommandLine.Option(names = ["-c", "--container"],
            description = ["Filtering the ReactantObject by container rawIdentifier, wildcard is available"])
    val containerIdentifierWildcards: ArrayList<String> = arrayListOf()

    @CommandLine.Parameters(arity = "0..*", paramLabel = "CLASS_NAME",
            description = ["Filtering ReactantObject class canonical name, wildcard is available"])
    var classNameWildcards: ArrayList<String> = arrayListOf();

    private val listTable = MultiColumns.create {
        column { align = MultiColumns.Alignment.Right }
        column { maxLength = 50; overflowCutFromRight = false; }
        column { maxLength = 40 }
        column { align = MultiColumns.Alignment.Center }
    }

    override fun run() {
        requirePermission(Reactant.REACTANT_OBJ.LIST)

        dependencyManager.dependencies.mapNotNull { it as? ReactantObjectInjectableWrapper<*> }
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
                .map { Pair(it, containerManager.containers.first { container -> it.productType.jvmErasure in container.reactantObjectClasses }) }
                // Container rawIdentifier filter
                .filter { matchContainerIdentifierWildcards(it.second.identifier) }
                .toList()
                .forEach { addToListTable(it) }
        listTable.generate().forEach(stdout::out)
    }

    private fun matchClassNameWildcards(reactantObjectWrapper: ReactantObjectInjectableWrapper<*>) =
            classNameWildcards.isEmpty() || classNameWildcards.any { wildcard ->
                PatternMatchingUtils.matchWildcard(wildcard, reactantObjectWrapper.productType.jvmErasure.jvmName)
            }

    private fun matchContainerIdentifierWildcards(identifier: String) =
            containerIdentifierWildcards.isEmpty() || containerIdentifierWildcards.any { wildcard ->
                PatternMatchingUtils.matchWildcard(wildcard, identifier)
            }

    private fun addToListTable(reactantObjectWrapperContainerPair: Pair<ReactantObjectInjectableWrapper<*>, Container>) {
        val reactantObjectWrapper = reactantObjectWrapperContainerPair.first;
        val container = reactantObjectWrapperContainerPair.second;
        listTable.rows.add(listOf<String>(
                reactantObjectWrapper.hashCode().toString(36),
                reactantObjectWrapper.productType.jvmErasure.let { if (showShortName) it.simpleName!! else it.jvmName },
                container.identifier,
                if (reactantObjectWrapper.isInitialized()) "Running"
                else if (reactantObjectWrapper.catchedThrowable != null) "Error"
                else if (!reactantObjectWrapper.fulfilled) "Not Fulfilled" else ""
        ))
    }

}
