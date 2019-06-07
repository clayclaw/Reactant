package net.swamphut.swampium.core.commands.swobject

import net.swamphut.swampium.core.commands.SwampiumPermissions.Companion.SWAMPIUM
import net.swamphut.swampium.core.dependency.DependencyManager
import net.swamphut.swampium.core.dependency.injection.producer.SwObjectInjectableWrapper
import net.swamphut.swampium.core.swobject.container.Container
import net.swamphut.swampium.core.swobject.container.ContainerManager
import net.swamphut.swampium.extra.command.SwCommand
import net.swamphut.swampium.utils.PatternMatchingUtils
import net.swamphut.swampium.utils.formatting.MultiColumns
import picocli.CommandLine
import java.util.regex.Pattern
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.jvmName

@CommandLine.Command(
        name = "ls",
        aliases = ["list"],
        mixinStandardHelpOptions = true,
        description = ["Listing all SwObject"]
)
class SwObjectListSubcommand(
        val dependencyManager: DependencyManager,
        val containerManager: ContainerManager
) : SwCommand() {

    @CommandLine.Option(names = ["-r", "--is-running"], paramLabel = "IS_RUNNING",
            description = ["Filtering SwObject by running state"])
    var isRunning: Boolean? = null

    @CommandLine.Option(names = ["-p", "--pattern"], paramLabel = "REG_EXP",
            description = ["Filtering SwObject class canonical name by RegExp"])
    var classNamePattern: Pattern? = null

    @CommandLine.Option(names = ["-o", "--short"],
            description = ["Display class short name instead of canonical name"])
    var showShortName: Boolean = false

    @CommandLine.Option(names = ["-c", "--container"],
            description = ["Filtering the SwObject by container rawIdentifier, wildcard is available"])
    val containerIdentifierWildcards: ArrayList<String> = arrayListOf()

    @CommandLine.Parameters(arity = "0..*", paramLabel = "CLASS_NAME",
            description = ["Filtering SwObject class canonical name, wildcard is available"])
    var classNameWildcards: ArrayList<String> = arrayListOf();

    private val listTable = MultiColumns.create {
        column { align = MultiColumns.Alignment.Right }
        column { maxLength = 50; overflowCutFromRight = false; }
        column { maxLength = 40 }
        column { align = MultiColumns.Alignment.Center }
    }

    override fun run() {
        requirePermission(SWAMPIUM.SWOBJECT.LIST)

        dependencyManager.dependencies.mapNotNull { it as? SwObjectInjectableWrapper<*> }
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
                .map { Pair(it, containerManager.containers.first { container -> it.productType.jvmErasure in container.swObjectClasses }) }
                // Container rawIdentifier filter
                .filter { matchContainerIdentifierWildcards(it.second.identifier) }
                .toList()
                .forEach { addToListTable(it) }
        listTable.generate().forEach(stdout::out)
    }

    private fun matchClassNameWildcards(swObjectWrapper: SwObjectInjectableWrapper<*>) =
            classNameWildcards.isEmpty() || classNameWildcards.any { wildcard ->
                PatternMatchingUtils.matchWildcard(wildcard, swObjectWrapper.productType.jvmErasure.jvmName)
            }

    private fun matchContainerIdentifierWildcards(identifier: String) =
            containerIdentifierWildcards.isEmpty() || containerIdentifierWildcards.any { wildcard ->
                PatternMatchingUtils.matchWildcard(wildcard, identifier)
            }

    private fun addToListTable(swObjectWrapperContainerPair: Pair<SwObjectInjectableWrapper<*>, Container>) {
        val swObjectWrapper = swObjectWrapperContainerPair.first;
        val container = swObjectWrapperContainerPair.second;
        listTable.rows.add(listOf<String>(
                swObjectWrapper.hashCode().toString(36),
                swObjectWrapper.productType.jvmErasure.let { if (showShortName) it.simpleName!! else it.jvmName },
                container.identifier,
                if (swObjectWrapper.isInitialized()) "Running"
                else if (swObjectWrapper.catchedThrowable != null) "Error"
                else if (!swObjectWrapper.fulfilled) "Not Fulfilled" else ""
        ))
    }

}
