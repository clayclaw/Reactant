package net.swamphut.swampium.core.swobject.container

interface ContainerManager {
    val containers: Collection<Container>

    fun getContainer(identifier: String): Container?

    /**
     * Search all swobject inside container and load into swobject manager
     */
    fun addContainer(container: Container)

    fun removeContainer(container: Container)
}
