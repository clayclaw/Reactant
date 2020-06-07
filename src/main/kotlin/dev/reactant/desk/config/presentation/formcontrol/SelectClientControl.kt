package dev.reactant.desk.config.presentation.formcontrol

import dev.reactant.desk.config.presentation.ClientControl

open class SelectClientControl(
        options: Map<String, String>
) : ClientControl("select") {
    var options: Map<String, String> by attributes

    init {
        this.options = options
    }
}
