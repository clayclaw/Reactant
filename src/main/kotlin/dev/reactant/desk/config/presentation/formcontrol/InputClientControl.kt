package dev.reactant.desk.config.presentation.formcontrol

import dev.reactant.desk.config.presentation.ClientControl

class InputClientControl(type: String = "text") : ClientControl("input") {
    var type: String by attributes

    init {
        this.type = type
    }
}
