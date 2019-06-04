package net.swamphut.swampium.core.commands

import net.swamphut.swampium.core.commands.swobject.SwObjectCommand
import net.swamphut.swampium.core.commands.swobject.SwObjectListSubcommand
import net.swamphut.swampium.core.dependency.injection.Inject
import net.swamphut.swampium.core.swobject.SwObjectManager
import net.swamphut.swampium.core.swobject.container.ContainerManager
import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleHook
import net.swamphut.swampium.extra.command.PicocliCommandService

@SwObject
internal class SwampiumCommandRegister : LifeCycleHook {
    @Inject
    private lateinit var commandService: PicocliCommandService

    @Inject
    private lateinit var swObjectManager: SwObjectManager

    @Inject
    private lateinit var containerManager: ContainerManager

    override fun init() {
        commandService.registerBy(this) {
            command(::SwObjectCommand) {
                subCommand({ SwObjectListSubcommand(swObjectManager, containerManager) })
            }
        }
    }
}
