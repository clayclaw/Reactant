package net.swamphut.swampium.repository.commands.repository

import net.swamphut.swampium.extra.command.SwCommand
import net.swamphut.swampium.repository.RepositoryService
import net.swamphut.swampium.repository.commands.RepositoryPermission.Companion.SWAMPIUM
import picocli.CommandLine

@CommandLine.Command(name = "add", mixinStandardHelpOptions = true, description = ["Add a maven repository"])
class RepositoryAddSubCommand(private val repositoryService: RepositoryService) : SwCommand() {
    @CommandLine.Option(names = ["-s", "--skip-checking"], description = ["Skip connection checking"])
    var skipConnectionChecking: Boolean = false

    @CommandLine.Option(names = ["-o", "--overwrite"], description = ["Overwrite repository url if name already exist"])
    var overwrite: Boolean = false

    @CommandLine.Parameters(index = "0", paramLabel = "NAME")
    lateinit var name: String

    @CommandLine.Parameters(index = "1", paramLabel = "URL")
    lateinit var url: String

    override fun run() {
        requirePermission(SWAMPIUM.REPOSITORY.MODIFY)
        if (!overwrite && repositoryService.getRepository(name) != null)
            stderr.out("Repository $name already exist, you can overwrite it with option '-o'");
        else repositoryService.setRepository(name, url, !skipConnectionChecking)
                .doOnError { stderr.out("Exception occurred: ${it.message}, consider use '-s' to skip connection checking.") }
                .subscribe { stdout.out("Repository $name added successfully") }
    }
}