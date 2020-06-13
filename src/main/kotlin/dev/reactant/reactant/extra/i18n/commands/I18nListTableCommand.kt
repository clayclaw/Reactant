package dev.reactant.reactant.extra.i18n.commands

import dev.reactant.reactant.core.commands.ReactantPermissions
import dev.reactant.reactant.extra.command.ReactantCommand
import dev.reactant.reactant.extra.i18n.I18nService
import dev.reactant.reactant.utils.PatternMatchingUtils
import picocli.CommandLine
import java.util.regex.Pattern

@CommandLine.Command(
        name = "ls",
        aliases = ["list"],
        mixinStandardHelpOptions = true,
        description = ["List i18n language tables"]
)
internal class I18nListTableCommand(
        private val i18nService: I18nService
) : ReactantCommand() {

    @CommandLine.Option(names = ["-p", "--pattern"], paramLabel = "REG_EXP",
            description = ["Filtering I18n Table class canonical name by RegExp"])
    var classNamePattern: Pattern? = null

    @CommandLine.Parameters(arity = "0..*", paramLabel = "CLASS_NAME",
            description = ["Filtering I18n Table class canonical name, wildcard is available"])
    var classNameWildcards: ArrayList<String> = arrayListOf();


    override fun run() {
        requirePermission(ReactantPermissions.ADMIN.DEV.I18N.LIST)
        i18nService.tableClasses.sortedBy { it.qualifiedName }
                .filter { nameMatching(it.java.canonicalName) }
                .forEach { stdout.out(it.java.canonicalName) }
    }

    private fun nameMatching(canonicalName: String): Boolean =
            (classNamePattern == null || classNamePattern!!.toRegex().matches(canonicalName)) &&
                    (classNameWildcards.isEmpty() || classNameWildcards
                            .any { wildcard -> PatternMatchingUtils.matchWildcard(wildcard, canonicalName) })
}
