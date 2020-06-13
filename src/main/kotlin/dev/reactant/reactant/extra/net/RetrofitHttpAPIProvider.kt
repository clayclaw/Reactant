package dev.reactant.reactant.extra.net

import dev.reactant.reactant.core.ReactantCore
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.injection.Provide
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.jvmErasure

@Component
private class RetrofitHttpAPIProvider {

    @Provide(".*", true)
    private fun provideComponents(kType: KType, name: String): RetrofitJsonAPI<Any> {
        val serviceClass = kType.arguments.first().type!!.jvmErasure as KClass<out Any>
        return RetrofitJsonAPIImpl(if (name != "") name else serviceClass.findAnnotation<BaseUrl>()?.baseUrl
                ?: "", serviceClass, kType.toString());
    }

    inner class RetrofitJsonAPIImpl<T : Any>(baseUrl: String, serviceClass: KClass<out T>, debugPrefix: String) : RetrofitJsonAPI<T> {
        @Suppress("UNCHECKED_CAST")
        override val service: T = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .let {
                    it.client(OkHttpClient.Builder().addNetworkInterceptor { chain ->
                        if (debugging) ReactantCore.logger.info("[$debugPrefix] ${chain.request()}")
                        chain.proceed(chain.request())
                    }.build())
                }
                .build().create(serviceClass.java) as T

        override var debugging: Boolean = false
    }
}
