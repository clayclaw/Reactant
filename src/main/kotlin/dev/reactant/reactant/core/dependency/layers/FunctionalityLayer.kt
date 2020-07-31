package dev.reactant.reactant.core.dependency.layers

import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.injection.components.Components

@Component
internal class FunctionalityLayer(private val systemLevelComponents: Components<SystemLevel>)
