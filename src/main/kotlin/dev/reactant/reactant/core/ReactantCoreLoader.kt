package dev.reactant.reactant.core

import dev.reactant.reactant.core.component.BukkitPluginContainerLoader
import io.izzel.taboolib.loader.Plugin
import io.izzel.taboolib.module.dependency.Dependencies
import io.izzel.taboolib.module.dependency.Dependency

@Dependencies(
    value = [
        Dependency(
            maven = "org.reactivestreams:reactive-streams:1.0.3",
            url = "https://repo1.maven.org/maven2/org/reactivestreams/reactive-streams/1.0.3/reactive-streams-1.0.3.jar"
        ),
        Dependency(
            maven = "io.reactivex.rxjava3:rxjava:3.0.11",
            url = "https://repo1.maven.org/maven2/io/reactivex/rxjava3/rxjava/3.0.11/rxjava-3.0.11.jar"
        ),
        Dependency(
            maven = "io.reactivex.rxjava3:rxkotlin:3.0.1",
            url = "https://repo1.maven.org/maven2/io/reactivex/rxjava3/rxkotlin/3.0.1/rxkotlin-3.0.1.jar"
        ),
        Dependency(
            maven = "net.oneandone.reflections8:reflections8:0.11.7",
            url = "https://repo1.maven.org/maven2/net/oneandone/reflections8/reflections8/0.11.7/reflections8-0.11.7.jar"
        ),
        Dependency(
            maven = "com.google.code.gson:gson:2.8.6",
            url = "https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.6/gson-2.8.6.jar"
        ),
        Dependency(
            maven = "org.yaml:snakeyaml:1.26",
            url = "https://repo1.maven.org/maven2/org/yaml/snakeyaml/1.26/snakeyaml-1.26.jar"
        ),
        Dependency(
            maven = "com.moandjiezana.toml:toml4j:0.7.2",
            url = "https://repo1.maven.org/maven2/com/moandjiezana/toml/toml4j/0.7.2/toml4j-0.7.2.jar"
        ),
        Dependency(
            maven = "info.picocli:picocli:4.3.2",
            url = "https://repo1.maven.org/maven2/info/picocli/picocli/4.3.2/picocli-4.3.2.jar"
        ),
        Dependency(
            maven = "org.apache.logging.log4j:log4j-core:2.12.1",
            url = "https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-core/2.12.1/log4j-core-2.12.1.jar"
        ),
        Dependency(
            maven = "org.javassist:javassist:3.26.0-GA",
            url = "https://repo1.maven.org/maven2/org/javassist/javassist/3.26.0-GA/javassist-3.26.0-GA.jar"
        )
    ]
)
@ReactantPlugin(servicePackages = ["dev.reactant.reactant"])
object ReactantCoreLoader: Plugin() {

    val reactantCore = ReactantCore(this.plugin)

    override fun onEnable() {
        reactantCore.onEnable()
        BukkitPluginContainerLoader.registerPlugin(this)
    }

}