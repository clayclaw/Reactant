package io.reactant.reactant.repository

import io.reactant.reactant.core.reactantobj.container.Reactant
import io.reactant.reactant.core.reactantobj.lifecycle.LifeCycleHook
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

@Reactant
class MavenRepositoryRetrieverService : LifeCycleHook {
    private val httpService = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl("http://localhost").build()
            .create(HttpMavenRepositoryRetrieverService::class.java)

    fun getArtifact(repositoryBaseUrl: String, group: String, artifactId: String, version: String, extension: String, classifier: String) =
            httpService.getArtifact("$repositoryBaseUrl/${group.replace(".", "/")}/$artifactId/$version/$artifactId-$version-$classifier.$extension")

    fun getArtifact(repositoryBaseUrl: String, group: String, artifactId: String, version: String, extension: String) =
            httpService.getArtifact("$repositoryBaseUrl/${group.replace(".", "/")}/$artifactId/$version/$artifactId-$version.$extension")

    internal interface HttpMavenRepositoryRetrieverService {
        @Streaming
        @GET
        fun getArtifact(@Url url: String): Single<ResponseBody>
    }
}

