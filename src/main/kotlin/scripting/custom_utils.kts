import dev.reactant.reactant.extra.script.kotlin.ScriptLibrary
import dev.reactant.reactant.extra.script.kotlin.scripting
import dev.reactant.skill.EffectsLibrary
import org.bukkit.Effect
import org.bukkit.Location


scripting<ScriptLibrary<EffectsLibrary>> {
    export = object : EffectsLibrary {
        override val effects: Map<String, (Location) -> Unit> = mapOf(
                "smoke" to { loc: Location -> loc.world!!.playEffect(loc, Effect.SMOKE, 0) }
        )
    }
}
