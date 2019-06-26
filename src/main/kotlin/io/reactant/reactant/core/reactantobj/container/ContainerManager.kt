package io.reactant.reactant.core.reactantobj.container

import io.reactant.reactant.core.dependency.injection.producer.InjectableWrapper

interface ContainerManager {
    val containers: Collection<Container>

    fun getContainer(identifier: String): Container?

    /**
     * Search all reactantobj inside container and load into reactantobj manager
     */
    fun addContainer(container: Container)

    fun removeContainer(container: Container)
    fun getContainerProvidedInjectableWrapper(container: Container): Set<InjectableWrapper>
}
