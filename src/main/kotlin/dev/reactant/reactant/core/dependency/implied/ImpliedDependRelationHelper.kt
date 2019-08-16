package dev.reactant.reactant.core.dependency.implied

import dev.reactant.reactant.core.dependency.injection.InjectRequirement
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

class ImpliedDependRelationHelper {
    companion object {
        fun getImpliedDependRequirementsRecursively(
//                providerManager: ProviderManager,
//                providerRelationManager: ProviderRelationManager,
//                componentProvider: ComponentProvider<*>,
                requirement: InjectRequirement): HashSet<InjectRequirement> {
            return walkType(requirement.requiredType);
        }

        private fun walkType(type: KType): HashSet<InjectRequirement> {
            val result = HashSet<InjectRequirement>();
            type.jvmErasure.java.let {
                if (it.isAnnotationPresent(ImpliedDepend::class.java)) {
                    val depend = it.getAnnotation(ImpliedDepend::class.java)
                    if (depend.nullableArgumentIndexes.size > 1) throw UnsupportedOperationException("Nullable implied depend is not implemented");
                    depend.typeArgumentIndexes.forEach { argIndex ->
                        checkArgSizeAndType(type, argIndex);

                        result.add(InjectRequirement(type.arguments[argIndex].type!!, ""));
                        walkType(type.arguments[argIndex].type!!);
                    }
                }
                if (it.isAnnotationPresent(ImpliedDependAll::class.java)) {
                    throw UnsupportedOperationException("Not implemented");
                }
            }
            return result;
        }

        private fun checkArgSizeAndType(type: KType, index: Int) {
            if (index >= type.arguments.size)
                throw IllegalArgumentException("Argument size of ${type.jvmErasure} is ${type.arguments.size}, but ImpliedDepend args included index $index");
            if (type.arguments[index].type == null)
                throw IllegalArgumentException("ImpliedDepend type ${type.jvmErasure} is required Generic args");
        }
    }
}
