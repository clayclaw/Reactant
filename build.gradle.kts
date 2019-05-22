import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

group = "net.swamphut"
version = "0.0.3"

val kotlinVersion = "1.3.31"


buildscript {
    repositories {
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:4.0.3")
    }
}

plugins {
    java
    maven
    `maven-publish`
    kotlin("jvm") version "1.3.31"
    id("com.jfrog.bintray") version "1.8.4"
}

apply(plugin = "maven-publish")
apply(plugin = "com.github.johnrengelman.shadow")

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val compileKotlin by tasks.getting(KotlinCompile::class) {
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
    ).forEach { compile(kotlin(it, kotlinVersion)) }

    implementation("org.bstats:bstats-bukkit:1.4")

    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.2.1")
    compile("io.reactivex.rxjava2:rxjava:2.2.0")
    compile("org.reflections:reflections:0.9.11")

    compile("com.google.code.gson:gson:2.8.5")
    compile("org.yaml:snakeyaml:1.24")

    compile ("info.picocli:picocli:4.0.0-alpha-3")
    compile ("org.mariadb.jdbc:mariadb-java-client:2.4.1")

    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
}

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

val shadowJar: ShadowJar by tasks
shadowJar.apply {
    baseName = project.name
    relocate("org.bstats", "net.swamphut.swampium.core")
}

tasks.getByName("build").dependsOn(shadowJar)


tasks.register<Copy>("deployPlugin") {
    dependsOn(shadowJar)
    val pluginDeployPath = System.getenv("PLUGIN_DEPLOY_PATH")

    if (pluginDeployPath != null) {
        from(shadowJar)
        into(pluginDeployPath)
    }
}
val deployPlugin = tasks.getByName("deployPlugin")
tasks.getByName("build").dependsOn(deployPlugin)

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
