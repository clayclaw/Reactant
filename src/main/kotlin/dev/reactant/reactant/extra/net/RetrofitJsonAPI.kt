package dev.reactant.reactant.extra.net


/**
 * Declare it is a retrofit json response type api
 * Use @Inject("API_BASE_URL") to specify base url if you would like to overwrite the base url provided by api service
 */
interface RetrofitJsonAPI<T : Any> {
    val service: T

    /**
     * Set true to log all the request
     */
    var debugging: Boolean
}

