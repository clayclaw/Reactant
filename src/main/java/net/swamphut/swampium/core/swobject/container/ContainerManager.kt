package net.swamphut.swampium.core.swobject.container

import net.swamphut.swampium.core.dependency.injection.producer.InjectableWrapper

interface ContainerManager {
    val containers: Collection<Container>

    fun getContainer(identifier: String): Container?

    /**
     * Search all swobject inside container and load into swobject manager
     */
    fun addContainer(container: Container)

    fun removeContainer(container: Container)
    fun getContainerProvidedInjectableWrapper(container: Container): Set<InjectableWrapper>
}
