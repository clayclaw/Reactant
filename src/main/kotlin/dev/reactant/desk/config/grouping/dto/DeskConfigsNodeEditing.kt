package dev.reactant.desk.config.grouping.dto

import com.google.gson.JsonObject
import dev.reactant.desk.config.presentation.ClientControl

data class DeskConfigsNodeEditing<T>(
        val controlRoot: ClientControl,
        val config: JsonObject
)
