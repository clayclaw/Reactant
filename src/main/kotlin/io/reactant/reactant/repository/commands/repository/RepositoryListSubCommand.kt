package io.reactant.reactant.repository.commands.repository

import io.reactant.reactant.extra.command.ReactantCommand
import io.reactant.reactant.repository.RepositoryService
import io.reactant.reactant.repository.commands.RepositoryPermission.Companion.Reactant
import io.reactant.reactant.utils.formatting.MultiColumns
import picocli.CommandLine

@CommandLine.Command(name = "list", aliases = ["ls"], mixinStandardHelpOptions = true)
class RepositoryListSubCommand(private val repositoryService: RepositoryService) : ReactantCommand() {
    override fun run() {
        repositoryService.consoleOnlyValidate(sender)
        requirePermission(Reactant.REPOSITORY.LIST)
        MultiColumns.create {
            column { }
            column { maxLength = 100 }
        }.apply {
            repositoryService.repositoriesMap.forEach { name, url -> rows.add(listOf(name, url)) }
            generate().forEach(stdout::out)
        }
    }
}