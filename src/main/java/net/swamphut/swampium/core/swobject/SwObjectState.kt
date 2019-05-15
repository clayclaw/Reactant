package net.swamphut.swampium.core.swobject

enum class SwObjectState {
    /**
     * The required service is not injected
     */
    Unsolved,
    /**
     * The object is not started or has been stopped
     */
    Inactive,
    /**
     * The object is available
     */
    Active
}
