package dev.reactant.reactant.example

import dev.reactant.reactant.extra.i18n.I18n
import dev.reactant.reactant.extra.i18n.I18nTable

@I18n("HelloTable")
open class HelloI18nTable : I18nTable {

    open fun blamePlayer(playerName: String, itemName: Int) = "$playerName destroyed the $itemName! ;C"

}
