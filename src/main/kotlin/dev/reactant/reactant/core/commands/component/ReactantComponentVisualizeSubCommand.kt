package dev.reactant.reactant.core.commands.component

import dev.reactant.reactant.core.commands.ReactantPermissions
import dev.reactant.reactant.core.dependency.ProviderManager
import dev.reactant.reactant.core.dependency.injection.producer.DynamicProvider
import dev.reactant.reactant.extra.command.ReactantCommand
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import org.bukkit.command.ConsoleCommandSender
import org.graphstream.graph.Edge
import org.graphstream.graph.implementations.SingleGraph
import org.graphstream.graph.implementations.SingleNode
import org.graphstream.ui.view.Viewer
import picocli.CommandLine

@CommandLine.Command(
        name = "visualize",
        aliases = ["visual"],
        mixinStandardHelpOptions = true,
        description = ["Show component relation with graphic"]
)
class ReactantComponentVisualizeSubCommand(
        val providerManager: ProviderManager
) : ReactantCommand() {
    override fun run() {
        requirePermission(ReactantPermissions.ADMIN.DEV.REACTANT_OBJ.VISUALIZE)
        if (sender !is ConsoleCommandSender) {
            stderr.out("Only console can use this command")
            return
        }

        Completable.fromAction {
            val graph = SingleGraph("Reactant Components Relation Visualization")
            providerManager.providers.forEach { provider ->
                graph.addNode<SingleNode>(provider.toString()).apply {
                    setAttribute("ui.label", provider.productType.toString())
                    when (provider) {
                        is DynamicProvider<*, *> -> addAttribute("ui.class", "important");
                    }
                    addAttribute("ui.class", "swobject")
                }
            }
            providerManager.interpretedRelations.forEach { relation ->
                val from = relation.interpretTarget.toString()
                val to = relation.dependOn.toString()
                graph.addEdge<Edge>("$from-$to", from, to, true).apply {
                    setAttribute("layout.weight", 3)
//                        if (it.lazyRequiredServices.contains(serivce)) {
//                            setAttribute("ui.label", "lazy")
//                        }
                }
            }
            graph.addAttribute("ui.quality");
            graph.addAttribute("ui.antialias");
            graph.display().closeFramePolicy = Viewer.CloseFramePolicy.HIDE_ONLY;

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread()).subscribe()


    }

}
