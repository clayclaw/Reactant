package dev.reactant.reactant.extra.i18n.commands

import I18nTranslation
import dev.reactant.reactant.core.ReactantCore
import dev.reactant.reactant.core.commands.ReactantPermissions
import dev.reactant.reactant.extra.command.ReactantCommand
import dev.reactant.reactant.extra.i18n.I18nService
import dev.reactant.reactant.extra.i18n.I18nTable
import dev.reactant.reactant.extra.parser.GsonJsonParserService
import dev.reactant.reactant.service.spec.config.ConfigService
import dev.reactant.reactant.service.spec.config.getOrDefault
import dev.reactant.reactant.utils.PatternMatchingUtils
import picocli.CommandLine
import java.io.File
import java.util.regex.Pattern
import kotlin.collections.set
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberFunctions

@CommandLine.Command(
    name = "generate",
    aliases = ["gen"],
    mixinStandardHelpOptions = true,
    description = ["Generate i18n language table"]
)
internal class I18nGenerateTableCommand(
    private val i18nService: I18nService,
    private val jsonParserService: GsonJsonParserService,
    private val configService: ConfigService
) : ReactantCommand(ReactantPermissions.ADMIN.DEV.I18N.GENERATE.toString()) {

    @CommandLine.Option(
        names = ["-l", "--language"], paramLabel = "LANGUAGE_CODE",
        description = ["Specify the language codes you want, default is 'en'"]
    )
    var languageCodes: ArrayList<String> = arrayListOf("en")

    @CommandLine.Option(
        names = ["-f", "--force"],
        description = ["Override and regenerate existing file"]
    )
    var force: Boolean = false

    @CommandLine.Option(
        names = ["-p", "--pattern"], paramLabel = "REG_EXP",
        description = ["Filtering I18n Table class canonical name by RegExp"]
    )
    var classNamePattern: Pattern? = null

    @CommandLine.Parameters(
        arity = "0..*", paramLabel = "CLASS_NAME",
        description = ["Filtering I18n Table class canonical name, wildcard is available"]
    )
    var classNameWildcards: ArrayList<String> = arrayListOf()

    override fun execute() {
        requirePermission(ReactantPermissions.ADMIN.DEV.I18N.GENERATE)
        i18nService.tableClasses.sortedBy { it.qualifiedName }
            .filter { nameMatching(it.java.canonicalName) }
            .forEach { tableClass: KClass<out I18nTable> ->
                ReactantCore.logger.info("Generated ${generate(tableClass)} language file for ${tableClass.qualifiedName}")
            }
    }

    private fun generate(tableClass: KClass<out I18nTable>): Int {
        val translationPairs = tableClass.declaredMemberFunctions.map { it.name to it.parameters.drop(1).map { "\$${it.name}" }.joinToString(" ") }
        languageCodes.map { code -> i18nService.getLanguageFilePath(tableClass, code) }
            .filter { force || !File(it).exists() }
            .onEach { path ->
                val translationFile = configService.getOrDefault(jsonParserService, path) { I18nTranslation() }.blockingGet()
                translationPairs.forEach { (key, value) ->
                    translationFile.content.translations[key] = value
                }
                translationFile.save().blockingAwait()
            }.let { return it.size }
    }

    private fun nameMatching(canonicalName: String): Boolean =
        (classNamePattern == null || classNamePattern!!.toRegex().matches(canonicalName)) &&
            (
                classNameWildcards.isEmpty() || classNameWildcards
                    .any { wildcard -> PatternMatchingUtils.matchWildcard(wildcard, canonicalName) }
                )
}
