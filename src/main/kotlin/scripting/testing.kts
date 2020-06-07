import dev.reactant.reactant.extra.config.ReactantConfigService
import dev.reactant.reactant.extra.script.kotlin.scripting
import dev.reactant.skill.EffectsLibrary
import dev.reactant.skill.ScriptCarrier
import dev.reactant.skill.SkillScripting
import dev.reactant.skill.SkillScriptingModule
import org.bukkit.entity.Player

ScriptCarrier(
        scripting<SkillScripting> {
            val configService = require<ReactantConfigService>()
            val otherScript = import<EffectsLibrary>("./CustomUtils.kts")

            export = object : SkillScriptingModule {
                override val displayName = "Testing"
                override fun release(player: Player) {
                    otherScript.effects["smoke"]?.let { it(player.location) }
                }
            }
        }
)

