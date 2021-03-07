package dev.reactant.reactant.repository.commands.repository

import dev.reactant.reactant.core.commands.ReactantPermissions
import dev.reactant.reactant.extra.command.ReactantCommand
import dev.reactant.reactant.repository.RepositoryService
import dev.reactant.reactant.utils.formatting.MultiColumns
import picocli.CommandLine

@CommandLine.Command(name = "list", aliases = ["ls"], mixinStandardHelpOptions = true)
class RepositoryListSubCommand(private val repositoryService: RepositoryService) :
    ReactantCommand(ReactantPermissions.REPOSITORY.LIST.toString()) {
    override fun execute() {
        repositoryService.consoleOnlyValidate(sender)
        requirePermission(ReactantPermissions.REPOSITORY.LIST)
        MultiColumns.create {
            column { }
            column { maxLength = 100 }
        }.apply {
            repositoryService.repositoriesMap.forEach { name, url -> rows.add(listOf(name, url)) }
            generate().forEach(stdout::out)
        }
    }
}
