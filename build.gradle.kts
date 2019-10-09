import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

group = "dev.reactant"
version = "0.1.5-SNAPSHOT"

val kotlinVersion = "1.3.31"

plugins {
    java
    `maven-publish`
    kotlin("jvm") version "1.3.31"
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs = listOf("-Xjvm-default=compatibility")
}

repositories {
    mavenCentral()
    maven { url = URI.create("https://hub.spigotmc.org/nexus/content/repositories/snapshots") }
    maven { url = URI.create("https://oss.sonatype.org/content/repositories/snapshots/") }
    maven { url = URI.create("https://repo.codemc.org/repository/maven-public") }
}

dependencies {
    listOf(
            "stdlib-jdk8",
            "reflect"
//            "script-util",
//            "script-runtime",
//            "compiler-embeddable",
//            "scripting-compiler"
    ).forEach { api(kotlin(it, kotlinVersion)) }

    implementation("org.bstats:bstats-bukkit:1.4") {
        isTransitive = false
    }

    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.2.1")
    api("io.reactivex.rxjava2:rxjava:2.+")
    api("io.reactivex.rxjava2:rxkotlin:2.+")
    api("org.reflections:reflections:0.9.11")

    api("com.google.code.gson:gson:2.8.5")
    api("org.yaml:snakeyaml:1.+")
    api("com.moandjiezana.toml:toml4j:0.7.+")

    api("info.picocli:picocli:4.+")
    api("org.mariadb.jdbc:mariadb-java-client:2.+")

    api("org.apache.logging.log4j:log4j-core:2.+")

    api("com.squareup.retrofit2:retrofit:2.+")
    api("com.squareup.retrofit2:adapter-rxjava2:2.+")
    api("com.squareup.retrofit2:converter-gson:2.+")

    compileOnly("org.spigotmc:spigot-api:1.14.2-R0.1-SNAPSHOT")
}

val sourcesJar by tasks.registering(Jar::class) {
    dependsOn(JavaPlugin.CLASSES_TASK_NAME)
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val shadowJar = (tasks["shadowJar"] as ShadowJar).apply {
    relocate("org.bstats", "dev.reactant.reactant.core")
    relocate("okhttp3", "dev.reactant.reactant.okhttp3")
    relocate("okio", "dev.reactant.reactant.okio")
}

val deployPlugin by tasks.registering(Copy::class) {
    dependsOn(shadowJar)
    System.getenv("PLUGIN_DEPLOY_PATH")?.let {
        from(shadowJar)
        into(it)
    }
}

val build = (tasks["build"] as Task).apply {
    arrayOf(
            sourcesJar,
            shadowJar,
            deployPlugin
    ).forEach { dependsOn(it) }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(sourcesJar.get())
            artifact(shadowJar)

            groupId = group.toString()
            artifactId = project.name
            version = version
        }
    }

    repositories {
        maven {
            url = uri("https://gitlab.com/api/v4/projects/${System.getenv("CI_PROJECT_ID")}/packages/maven")
            credentials(HttpHeaderCredentials::class) {
                name = "Private-Token"
                value = System.getenv("CI_JOB_TOKEN")
            }
            authentication {
                create<HttpHeaderAuthentication>("header")
            }
        }
    }
}

