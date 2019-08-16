package dev.reactant.reactant.repository.commands.repository

import dev.reactant.reactant.extra.command.ReactantCommand
import dev.reactant.reactant.repository.RepositoryService
import dev.reactant.reactant.repository.commands.RepositoryPermission.Companion.Reactant
import picocli.CommandLine

@CommandLine.Command(name = "add", mixinStandardHelpOptions = true, description = ["Add a maven repository"])
class RepositoryAddSubCommand(private val repositoryService: RepositoryService) : ReactantCommand() {
    @CommandLine.Option(names = ["-s", "--skip-checking"], description = ["Skip connection checking"])
    var skipConnectionChecking: Boolean = false

    @CommandLine.Option(names = ["-o", "--overwrite"], description = ["Overwrite repository url if name already exist"])
    var overwrite: Boolean = false

    @CommandLine.Parameters(index = "0", paramLabel = "NAME")
    lateinit var name: String

    @CommandLine.Parameters(index = "1", paramLabel = "URL")
    lateinit var url: String

    override fun run() {
        repositoryService.consoleOnlyValidate(sender)
        requirePermission(Reactant.REPOSITORY.MODIFY)
        if (!overwrite && repositoryService.getRepository(name) != null)
            stderr.out("Repository $name already exist, you can overwrite it with option '-o'");
        else repositoryService.setRepository(name, url, !skipConnectionChecking)
                .doOnError { stderr.out("Exception occurred: ${it.message}, consider use '-s' to skip connection checking.") }
                .subscribe { stdout.out("Repository $name added successfully") }
    }
}
