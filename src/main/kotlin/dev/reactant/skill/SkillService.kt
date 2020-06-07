package dev.reactant.skill

import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleControlAction
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import dev.reactant.reactant.core.component.lifecycle.LifeCycleInspector
import dev.reactant.reactant.extra.command.PicocliCommandService
import dev.reactant.reactant.extra.command.ReactantCommand
import dev.reactant.reactant.service.spec.dsl.register
import dev.reactant.reactant.service.spec.script.kotlin.KtsService
import org.bukkit.entity.Player
import picocli.CommandLine
import java.io.File

@Component
class SkillService(
        val ktsService: KtsService,
        val commandService: PicocliCommandService
) : LifeCycleInspector, LifeCycleHook {
    override fun onEnable() {
        register(commandService) {
            command(::SkillCommand) {
                command({ SkillReloadCommand(ktsService) })
                command({ SkillReleaseCommand(ktsService) })
            }
        }
    }

    override fun afterBulkActionComplete(action: LifeCycleControlAction) {
        if (action == LifeCycleControlAction.Initialize) {
            val scriptFolder = File("plugins/Reactant/skills")
            scriptFolder.listFiles()?.forEach {
                ktsService.preload(it).blockingAwait()
            }
        }
    }
}

@CommandLine.Command(name = "skill", mixinStandardHelpOptions = true)
class SkillCommand : ReactantCommand() {
    override fun run() = Unit
}

@CommandLine.Command(name = "reload", mixinStandardHelpOptions = true)
class SkillReloadCommand(val ktsService: KtsService) : ReactantCommand() {
    @CommandLine.Parameters(arity = "1", paramLabel = "PATH")
    var filePath: String = "";

    override fun run() {
        ktsService.reload(File(filePath))
    }
}


@CommandLine.Command(name = "release", mixinStandardHelpOptions = true)
class SkillReleaseCommand(val ktsService: KtsService) : ReactantCommand() {
    @CommandLine.Parameters(arity = "1", paramLabel = "PATH")
    var filePath: String = "";

    override fun run() {
        ktsService.execute<SkillScriptingModule>(File(filePath)).blockingGet().release(sender as Player)
    }
}
