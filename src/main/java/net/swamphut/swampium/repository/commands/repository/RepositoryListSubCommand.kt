package net.swamphut.swampium.repository.commands.repository

import net.swamphut.swampium.extra.command.SwCommand
import net.swamphut.swampium.repository.RepositoryService
import net.swamphut.swampium.repository.commands.RepositoryPermission
import net.swamphut.swampium.utils.formatting.MultiColumns
import picocli.CommandLine

@CommandLine.Command(name = "list", aliases = ["ls"], mixinStandardHelpOptions = true)
class RepositoryListSubCommand(private val repositoryService: RepositoryService) : SwCommand() {
    override fun run() {
        repositoryService.consoleOnlyValidate(sender)
        requirePermission(RepositoryPermission.Companion.SWAMPIUM.REPOSITORY.LIST)
        MultiColumns.create {
            column { }
            column { maxLength = 100 }
        }.apply {
            repositoryService.repositoriesMap.forEach { name, url -> rows.add(listOf(name, url)) }
            generate().forEach(stdout::out)
        }
    }
}