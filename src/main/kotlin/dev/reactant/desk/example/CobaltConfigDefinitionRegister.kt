package dev.reactant.desk.example

import dev.reactant.desk.config.ConfigDefinitionService
import dev.reactant.desk.config.tree.leaf.multi.ElementTypeDefinition
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import dev.reactant.reactant.core.dependency.injection.Inject
import dev.reactant.reactant.extra.config.type.MultiConfigs
import dev.reactant.reactant.service.spec.dsl.register
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

@Component
class CobaltConfigDefinitionRegister(
        val configDefinitionService: ConfigDefinitionService,
        @Inject("plugins/Cobalt/items") val cobaltItemConfigs: MultiConfigs<ConfigItem>
) : LifeCycleHook {
    override fun onEnable() {
        register(configDefinitionService) {
            group("cobalt", "Cobalt") {
                multiConfig("item", "Items",
                        cobaltItemConfigs,
                        { ConfigItem() }) {

                    ConfigItem::cobaltId text { displayName = "Item ID" }

                    ConfigItem::type staticSelect {
                        displayName = "Item material type"
                        description = "How should the item look like"
                        Material.values().forEach { material -> option(material.name, material.toString()) }
                    }

                    ConfigItem::displayName text { displayName = "Item display name" }
                    ConfigItem::lore textArea { displayName = "Item lore" }

                    ConfigItem::enchants
                    ConfigItem::tags

                    ConfigItem::unbreakable checkbox { displayName = "Unbreakable" }

                    ConfigItem::specialModifier extract {
                        this expansionPanel {
                            displayName = "Special modifier"
                            ConfigItem.SpecialModifier::requireUpdateLores checkbox {}
                            ConfigItem.SpecialModifier::levelRequired numberInt {}
                            ConfigItem.SpecialModifier::additionSkillSlot numberInt {}
                            ConfigItem.SpecialModifier::additionSkillModifierSlot numberInt {}

                            ConfigItem.SpecialModifier::boundSkills staticMultiSelect {
                                displayName = "Item skills"
                                // get skills and loop option
                                option("Fake Skill", "fake-skill-id")
                            }

                            ConfigItem.SpecialModifier::boundSkillModifiers listItems {
                                displayName = "Item skills modifiers"
                                genericT.apply {
                                    ElementTypeDefinition<SkillModifier>::element extract {
                                        SkillModifier::key text { displayName = "Modifier key" }
                                        SkillModifier::value numberInt { displayName = "Modifier value" }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }

}


class ConfigItem {
    var cobaltId: String = "unknown"
    var type: String = Material.GRASS_BLOCK.toString()

    var displayName: String = "unknown"
    var lore: ArrayList<String> = arrayListOf()

    // Enchantments' key are using Namespaced key
    var enchants: HashMap<String, Int> = hashMapOf(
            Enchantment.DAMAGE_ALL.key.key to 10
    )

    var tags: HashMap<String, String> = hashMapOf(
            "test" to "this is test tag"
    )

    var unbreakable: Boolean = false
    var specialModifier = SpecialModifier()

    class SpecialModifier {

        var requireUpdateLores = false
        var levelRequired = 0
        var additionSkillSlot = 0
        var additionSkillModifierSlot = 0
        var boundSkills: ArrayList<String> = arrayListOf()
        var boundSkillModifiers: ArrayList<SkillModifier> = arrayListOf()

        companion object {
            const val KEY_REQUIREUPDATELORES = "constantLoreUpdate"
            const val KEY_LEVELREQUIRED = "levelRequired"
            const val KEY_BOUNDSKILLS = "boundSkills"
            const val KEY_BOUNDSKILLMODIFIERS = "boundSkillModifiers"
            const val KEY_ADDITION_SKILL_SLOT = "additionSkillSlot"
            const val KEY_ADDITION_SKILL_MOD_SLOT = "additionSkillModifierSlot"
        }
    }
}

class SkillModifier {

    var key: String = "unknown"
    var value: Int = 0

    fun serialize() = "$key $value"

}
