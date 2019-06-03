package net.swamphut.swampium.core.commands.swobject

import net.swamphut.swampium.core.commands.SwampiumPermissions.Companion.SWAMPIUM
import net.swamphut.swampium.core.swobject.SwObjectInfo
import net.swamphut.swampium.core.swobject.SwObjectManager
import net.swamphut.swampium.core.swobject.SwObjectState
import net.swamphut.swampium.core.swobject.container.Container
import net.swamphut.swampium.core.swobject.container.ContainerManager
import net.swamphut.swampium.extra.command.SwCommand
import net.swamphut.swampium.utils.PatternMatchingUtils
import net.swamphut.swampium.utils.formatting.MultiColumns
import picocli.CommandLine
import java.util.regex.Pattern

@CommandLine.Command(
        name = "ls",
        aliases = ["list"],
        mixinStandardHelpOptions = true,
        description = ["Listing all SwObject"]
)
class SwObjectListSubcommand(
        val swObjectManager: SwObjectManager,
        val containerManager: ContainerManager
) : SwCommand() {

    @CommandLine.Option(names = ["-s", "--state"], paramLabel = "STATE")
    var states: ArrayList<SwObjectState> = arrayListOf()

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
        column { maxLength = 30; overflowCutFromRight = false; }
        column { maxLength = 30 }
        column { align = MultiColumns.Alignment.Center }
        column {}
        column {}
    }

    override fun run() {
        requirePermission(SWAMPIUM.SWOBJECT.LIST)

        swObjectManager.swObjectClassMap.values
                .asSequence()
                // State filter
                .filter { states.isEmpty() || states.contains(it.state) }
                // Class name pattern filter
                .filter { classNamePattern == null || classNamePattern!!.toRegex().matches(it.instanceClass.canonicalName) }
                // Class name wildcards filter
                .filter { matchClassNameWildcards(it) }
                // wrap it with its container
                .map { Pair(it, containerManager.containers.first { container -> it.instanceClass in container.swObjectClasses }) }
                // Container rawIdentifier filter
                .filter { matchContainerIdentifierWildcards(it.second.identifier) }
                .toList()
                .forEach { addToListTable(it) }
        listTable.generate().forEach(stdout::out)
    }

    private fun matchClassNameWildcards(swObjectInfo: SwObjectInfo<Any>) =
            classNameWildcards.isEmpty() || classNameWildcards.any { wildcard ->
                PatternMatchingUtils.matchWildcard(wildcard, swObjectInfo.instanceClass.canonicalName)
            }

    private fun matchContainerIdentifierWildcards(identifier: String) =
            containerIdentifierWildcards.isEmpty() || containerIdentifierWildcards.any { wildcard ->
                PatternMatchingUtils.matchWildcard(wildcard, identifier)
            }

    private fun addToListTable(swObjectContainerPair: Pair<SwObjectInfo<Any>, Container>) {
        val swObject = swObjectContainerPair.first;
        val container = swObjectContainerPair.second;
        listTable.rows.add(listOf<String>(
                swObject.instance.hashCode().toString(36),
                swObject.instanceClass.let { if (showShortName) it.simpleName else it.canonicalName },
                container.identifier,
                swObject.state.toString(),
                if (swObject.lifeCycleActionExceptions.size > 0) "${swObject.lifeCycleActionExceptions.size} Error"
                else "",
                if (swObject.fulfilled) "" else "Not Fulfilled"
        ))
    }

}
