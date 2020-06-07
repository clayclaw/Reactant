package dev.reactant.reactant.example

import org.bukkit.Effect

class ExampleConfig {
    var spawnWorld: String = ""
    var welcomeMessage: String = ""
    var welcomeEffect: Effect? = null
    var welcomeEffectData: Effect? = null
}

class DungeonConfig {
}

class MonsterConfig {
    var id: String = ""
    var name: String = ""
    var damage: Int = 0
}
