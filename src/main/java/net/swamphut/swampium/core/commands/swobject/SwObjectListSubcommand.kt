package net.swamphut.swampium.core.commands.swobject

import net.swamphut.swampium.core.commands.SwampiumPermissions.Companion.SWAMPIUM
import net.swamphut.swampium.core.swobject.SwObjectManager
import net.swamphut.swampium.core.swobject.SwObjectState
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
            description = ["Filtering the SwObject by container identifier, wildcard is available"])
    val containerIdentifierWildcards: ArrayList<String> = arrayListOf()

    @CommandLine.Parameters(arity = "0..*", paramLabel = "CLASS_NAME",
            description = ["Filtering SwObject class canonical name, wildcard is available"])
    var classNameWildcards: ArrayList<String> = arrayListOf();

    override fun run() {
        requirePermission(SWAMPIUM.SWOBJECT.LIST)

        val table = MultiColumns.create {
            column {
                align = MultiColumns.Alignment.Right
            }
            column {
                maxLength = 30
                overflowCutFromRight = false
            }
            column {
                maxLength = 30
            }
            column { align = MultiColumns.Alignment.Center }
            column {}
            column {}
        }

        swObjectManager.swObjectClassMap.values
                .asSequence()
                .filter { states.isEmpty() || states.contains(it.state) }
                .filter { classNamePattern == null || classNamePattern!!.toRegex().matches(it.instance.javaClass.canonicalName) }
                .filter {
                    classNameWildcards.isEmpty() || classNameWildcards.any { wildcard ->
                        PatternMatchingUtils.matchWildcard(wildcard, it.instance.javaClass.canonicalName)
                    }
                }
                .map { swObjectInfo ->
                    object {
                        val swObjectInfo = swObjectInfo
                        val container = containerManager.containers
                                .first { it.swObjectClasses.contains(swObjectInfo.instance.javaClass) }
                    }
                }
                .filter { swObjectWrapper ->
                    containerIdentifierWildcards.isEmpty() || containerIdentifierWildcards.any { wildcard ->
                        PatternMatchingUtils.matchWildcard(wildcard, swObjectWrapper.container.identifier)
                    }
                }
                .toList()
                .forEach { swObjectWrapper ->
                    table.rows.add(listOf<String>(
                            swObjectWrapper.swObjectInfo.instance.hashCode().toString(36),
                            swObjectWrapper.swObjectInfo.instance.javaClass.let { if (showShortName) it.simpleName else it.canonicalName },
                            swObjectWrapper.container.identifier,
                            swObjectWrapper.swObjectInfo.state.toString(),
                            "${swObjectWrapper.swObjectInfo.lifeCycleActionExceptions.size} Error",
                            if (swObjectWrapper.swObjectInfo.fulfilled) "" else "Not Fulfilled"
                    ))
                }
        table.generate().forEach(stdout::out)
    }
}
