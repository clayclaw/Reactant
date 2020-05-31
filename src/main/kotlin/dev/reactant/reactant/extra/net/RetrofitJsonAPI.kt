package dev.reactant.reactant.extra.net

import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.injection.Provide
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.jvmErasure

/**
 * Declare it is a retrofit json response type api
 * Use @Inject("API_BASE_URL") to specify base url if you would like to overwrite the base url provided by api service
 */
class RetrofitJsonAPI<T>(val service: T)

/**
 * Use to specify the base url of the api service interface
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class BaseUrl(val baseUrl: String)

@Component
private class RetrofitHttpAPIProvider {

    @Provide(".*", true)
    private fun provideComponents(kType: KType, name: String): RetrofitJsonAPI<Any> {
        val serviceClass = kType.arguments.first().type!!.jvmErasure
        val retrofit = Retrofit.Builder()
                .baseUrl(if (name != "") name else serviceClass.findAnnotation<BaseUrl>()?.baseUrl ?: "")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        return RetrofitJsonAPI(retrofit.create(serviceClass.java))
    }
}

