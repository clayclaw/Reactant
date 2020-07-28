package dev.reactant.reactant.core.commands

import dev.reactant.reactant.extra.command.PermissionRoot

object ReactantPermissions : PermissionRoot("reactant") {
    object ADMIN : S(prefix) {
        object DEV : S(prefix) {
            object OBJ : S(prefix) {
                object LIST : S(prefix)
                object STATUS : S(prefix)
                object VISUALIZE : S(prefix)
            }

            object PROFILER : S(prefix)
            object I18N : S(prefix) {
                object LIST : S(prefix)
                object GENERATE : S(prefix)
            }
        }
    }

    object REPOSITORY : S(prefix) {
        object LIST : S(prefix)
        object MODIFY : S(prefix)
        object RETRIEVE : S(prefix)
    }
}
