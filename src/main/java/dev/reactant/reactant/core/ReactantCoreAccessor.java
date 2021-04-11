package dev.reactant.reactant.core;

import dev.reactant.xaku.manager.dependency.annotation.Dependencies;
import dev.reactant.xaku.manager.dependency.annotation.Dependency;
import dev.reactant.xaku.manager.dependency.event.DependenciesLoadedEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Dependencies(value = {
        @Dependency(
                maven = "org.jetbrains.kotlin:kotlin-reflect:1.4.20",
                jarUrl = "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-reflect/1.4.20/kotlin-reflect-1.4.20.jar"
        ),
        @Dependency(
                maven = "org.jetbrains.kotlin:kotlin-stdlib:1.4.20",
                jarUrl = "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/1.4.20/kotlin-stdlib-1.4.20.jar"
        ),
        @Dependency(
                maven = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.20",
                jarUrl = "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-jdk8/1.4.20/kotlin-stdlib-jdk8-1.4.20.jar"
        ),
        @Dependency(
                maven = "com.squareup.okio:okio:1.17.2",
                jarUrl = "https://repo1.maven.org/maven2/com/squareup/okio/okio/1.17.2/okio-1.17.2.jar"
        ),
        @Dependency(
                maven = "com.squareup.okhttp3:okhttp:3.14.9",
                jarUrl = "https://repo1.maven.org/maven2/com/squareup/okhttp3/okhttp/3.14.9/okhttp-3.14.9.jar"
        ),
        @Dependency(
                maven = "com.squareup.retrofit2:retrofit:2.9.0",
                jarUrl = "https://repo1.maven.org/maven2/com/squareup/retrofit2/retrofit/2.9.0/retrofit-2.9.0.jar"
        ),
        @Dependency(
                maven = "com.squareup.retrofit2:adapter-rxjava3:2.9.0",
                jarUrl = "https://repo1.maven.org/maven2/com/squareup/retrofit2/adapter-rxjava3/2.9.0/adapter-rxjava3-2.9.0.jar"
        ),
        @Dependency(
                maven = "com.squareup.retrofit2:converter-gson:2.9.0",
                jarUrl = "https://repo1.maven.org/maven2/com/squareup/retrofit2/converter-gson/2.9.0/converter-gson-2.9.0.jar"
        ),
        @Dependency(
                maven = "org.reactivestreams:reactive-streams:1.0.3",
                jarUrl = "https://repo1.maven.org/maven2/org/reactivestreams/reactive-streams/1.0.3/reactive-streams-1.0.3.jar"
        ),
        @Dependency(
                maven = "io.reactivex.rxjava3:rxjava:3.0.11",
                jarUrl = "https://repo1.maven.org/maven2/io/reactivex/rxjava3/rxjava/3.0.11/rxjava-3.0.11.jar"
        ),
        @Dependency(
                maven = "io.reactivex.rxjava3:rxkotlin:3.0.1",
                jarUrl = "https://repo1.maven.org/maven2/io/reactivex/rxjava3/rxkotlin/3.0.1/rxkotlin-3.0.1.jar"
        ),
        @Dependency(
                maven = "org.reflections:reflections:0.9.12",
                jarUrl = "https://repo1.maven.org/maven2/org/reflections/reflections/0.9.12/reflections-0.9.12.jar"
        ),
        @Dependency(
                maven = "com.google.code.gson:gson:2.8.6",
                jarUrl = "https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.6/gson-2.8.6.jar"
        ),
        @Dependency(
                maven = "org.yaml:snakeyaml:1.26",
                jarUrl = "https://repo1.maven.org/maven2/org/yaml/snakeyaml/1.26/snakeyaml-1.26.jar"
        ),
        @Dependency(
                maven = "com.moandjiezana.toml:toml4j:0.7.2",
                jarUrl = "https://repo1.maven.org/maven2/com/moandjiezana/toml/toml4j/0.7.2/toml4j-0.7.2.jar"
        ),
        @Dependency(
                maven = "info.picocli:picocli:4.3.2",
                jarUrl = "https://repo1.maven.org/maven2/info/picocli/picocli/4.3.2/picocli-4.3.2.jar"
        ),
        @Dependency(
                maven = "org.apache.logging.log4j:log4j-core:2.12.1",
                jarUrl = "https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-core/2.12.1/log4j-core-2.12.1.jar"
        ),
        @Dependency(
                maven = "org.javassist:javassist:3.26.0-GA",
                jarUrl = "https://repo1.maven.org/maven2/org/javassist/javassist/3.26.0-GA/javassist-3.26.0-GA.jar"
        )
})
@ReactantPlugin(servicePackages = { "dev.reactant.reactant" })
public class ReactantCoreAccessor extends JavaPlugin implements Listener {

    private static ReactantCore reactantCore;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onDependenciesLoaded(DependenciesLoadedEvent e) {
        reactantCore = new ReactantCore(this);
        reactantCore.onEnable();
    }
}
