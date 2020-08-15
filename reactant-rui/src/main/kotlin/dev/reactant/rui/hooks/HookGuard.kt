import dev.reactant.rui.render.RuiNodeState
import dev.reactant.rui.render.ruiRenderGlobalState

fun <T> hookGuard(action: RuiNodeState.() -> T): T {
    return ruiRenderGlobalState?.currentNodeState.let { currentNodeState ->
        if (currentNodeState == null) throw IllegalStateException("Are you trying to access useState() outside rui component?")
        action(currentNodeState)
    }
}
