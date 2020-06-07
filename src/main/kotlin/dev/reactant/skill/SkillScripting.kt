package dev.reactant.skill

import dev.reactant.reactant.service.spec.script.kotlin.KtsService
import org.bukkit.Location
import org.bukkit.entity.Player
import kotlin.script.experimental.annotations.KotlinScript


interface SkillScriptingModule {
    val displayName: String
    fun release(player: Player)
}

interface EffectsLibrary {
    val effects: Map<String, (Location) -> Unit>
}

class SkillScripting : KtsService.Scripting<SkillScriptingModule>() {

}

class ScriptCarrier<T : KtsService.Scripting<out Any>>(
        val main: (T?, KtsService.ScriptImporter) -> T
) {
}

@KotlinScript(fileExtension = "kts")
abstract class SimpleScript
