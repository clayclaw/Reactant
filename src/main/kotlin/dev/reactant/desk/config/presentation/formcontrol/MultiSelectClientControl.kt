package dev.reactant.desk.config.presentation.formcontrol

class MultiSelectClientControl(
        options: Map<String, String>
) : SelectClientControl(options) {
    override var controlType: String = "list"
}
