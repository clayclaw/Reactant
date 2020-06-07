package dev.reactant.desk.api

import dev.reactant.desk.config.ConfigDefinitionService
import dev.reactant.desk.config.grouping.DeskObjectEntityNode
import dev.reactant.reactant.core.ReactantCore
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import io.ktor.application.ApplicationStopping
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.reactivex.Completable
import java.lang.reflect.Modifier

@Component
class DeskAPIServer(private val configDefinitionService: ConfigDefinitionService) : LifeCycleHook {

    val port = 8655
    lateinit var server: NettyApplicationEngine

    override fun onEnable() {
        embeddedServer(Netty, 8655) {
            install(ContentNegotiation) {
                gson {
                    excludeFieldsWithModifiers(Modifier.TRANSIENT)
                }
            }
            install(CORS) {
                method(HttpMethod.Options)
                method(HttpMethod.Get)
                method(HttpMethod.Post)
                method(HttpMethod.Put)
                method(HttpMethod.Delete)
                method(HttpMethod.Patch)
                header(HttpHeaders.AccessControlAllowHeaders)
                header(HttpHeaders.ContentType)
                header(HttpHeaders.AccessControlAllowOrigin)
                anyHost()
            }
            routing {
                get("/config") {
                    context.respond(configDefinitionService.rootNode.toSnapshot())
                }
                get("/config/{configPath...}") {
                    context.parameters.getAll("configPath")?.let { paths ->
                        (configDefinitionService.rootNode.getByPath(paths) as? DeskObjectEntityNode<*>)?.toEditing()
                                ?.let { context.respond(it) }
                                ?: context.respond(HttpStatusCode.NotFound)
                    }
                }
            }
        }.apply {
            start(false)
            server = this
        }
    }

    override fun onDisable() {
        Completable.create { source ->
            server.environment.monitor.subscribe(ApplicationStopping) { source.onComplete() }
            server.stop(1000, 2000)
        }.blockingAwait()
    }
}
