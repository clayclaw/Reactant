package dev.reactant.desk.config.presentation

import com.sun.org.apache.xpath.internal.operations.Bool

abstract class ClientControl(open var controlType: String) {
    var attributes: HashMap<String, Any?> = HashMap()
    open var children: List<ClientControl> = arrayListOf()

    @delegate:Transient
    var displayName: String? by attributes

    @delegate:Transient
    var description: String? by attributes

    @delegate:Transient
    var clientSideValidationCode: String? by attributes

    @delegate:Transient
    var propertyKey: String? by attributes

    @delegate:Transient
    var nullable: Bool? by attributes
}
