package dev.reactant.desk.config.tree

import com.google.gson.JsonElement
import dev.reactant.desk.config.presentation.ClientControl
import dev.reactant.desk.config.tree.branch.DefinitionBranchNode
import java.util.*
import kotlin.reflect.KClass

abstract class DefinitionNode<T, P : ClientControl>(
        @Transient val parent: DefinitionBranchNode<*, *>?,
        @Transient val objectClass: KClass<*>
) {
    var displayName: String = ""
    var description: String = ""

    open val definitions: ArrayList<DefinitionNode<*, *>> = arrayListOf()

    /**
     * Javascript that will be eval to validate the object/value in client side
     * The validator should receive the value object, and return an array of errors message (empty array if pass)
     * Example: "(num) => num % 2 == 0 ? ["Only odd number allowed"] : []"
     * Example2: "(item) => item.type == "APPLE" && item.amount != 1 ? ["You can't have more than 1 Apple"] : []"
     */
    var clientSideValidationCode: String? = null

    abstract fun convertFromJson(from: JsonElement): T
    abstract fun convertToJson(from: T): JsonElement

    fun toClientControl(): P = clientControlFactory().apply(clientControlModifier)
    abstract val clientControlFactory: () -> P
    open val clientControlModifier: P.() -> Unit = {
        this.displayName = this@DefinitionNode.displayName
        this.description = this@DefinitionNode.description
        this.clientSideValidationCode = this@DefinitionNode.clientSideValidationCode
    }

}
