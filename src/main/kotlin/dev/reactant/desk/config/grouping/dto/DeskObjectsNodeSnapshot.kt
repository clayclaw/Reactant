package dev.reactant.desk.config.grouping.dto

interface DeskObjectsNodeSnapshot {
    val identifier: String
    val name: String
    val type: String
}

data class DeskObjectsGroupNodeSnapshot(
        override val identifier: String,
        override val name: String,
        val children: Map<String, DeskObjectsNodeSnapshot> = hashMapOf()
) : DeskObjectsNodeSnapshot {
    override val type = "group"
}

data class DeskSingleObjectNodeSnapshot(
        override val identifier: String,
        override val name: String
) : DeskObjectsNodeSnapshot {
    override val type = "single"
}

data class DeskMultiObjectsNodeSnapshot(
        override val identifier: String,
        override val name: String,
        val objects: Map<String, String>
) : DeskObjectsNodeSnapshot {
    override val type = "multi"
}
