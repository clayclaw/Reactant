import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

group = "net.swamphut"
version = "0.0.5"

val kotlinVersion = "1.3.31"

plugins {
    java
    `maven-publish`
    kotlin("jvm") version "1.3.31"
    id("com.jfrog.bintray") version "1.8.4"
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
    maven { url = URI.create("https://dl.bintray.com/setako/swamphut") }
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
    api("io.reactivex.rxjava2:rxjava:2.2.0")
    api("org.reflections:reflections:0.9.11")

    api("com.google.code.gson:gson:2.8.5")
    api("org.yaml:snakeyaml:1.24")
    api("com.moandjiezana.toml:toml4j:0.7.2")

    api("info.picocli:picocli:4.0.0-alpha-3")
    api("org.mariadb.jdbc:mariadb-java-client:2.4.1")

    api("org.apache.logging.log4j:log4j-core:2.11.2")

    api("com.squareup.retrofit2:retrofit:2.5.0")
    api("com.squareup.retrofit2:adapter-rxjava2:2.5.0")
    api("com.squareup.retrofit2:converter-gson:2.5.0")

    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
}

val sourcesJar by tasks.registering(Jar::class) {
    dependsOn(JavaPlugin.CLASSES_TASK_NAME)
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val shadowJar = (tasks["shadowJar"] as ShadowJar).apply {
    relocate("org.bstats", "net.swamphut.swampium.core")
    relocate("okhttp3", "net.swamphut.swampium.okhttp3")
    archiveClassifier.set("all")
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
}

bintray {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_KEY")
    setPublications("maven")
    publish = true
    override = true
    pkg.apply {
        repo = "swamphut"
        name = project.name
        setLicenses("GPL-3.0")
        vcsUrl = "https://github.com/SwampHut/Swampium.git"
    }
}
