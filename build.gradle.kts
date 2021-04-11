import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

val kotlinVersion = "1.4.21"

group = "dev.reactant"

plugins {
    java
    `maven-publish`
    signing
    kotlin("jvm") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "5.0.0"
    id("org.jetbrains.dokka") version "0.10.0"
    id("com.palantir.git-version") version "0.12.3"
}

group = "dev.reactant"
val gitVersion: groovy.lang.Closure<String> by extra
val versionDetails: groovy.lang.Closure<String> by extra
val details = versionDetails() as com.palantir.gradle.gitversion.VersionDetails
version = details.lastTag + if (!details.isCleanTag && !details.lastTag.endsWith("-SNAPSHOT")) "-SNAPSHOT" else ""
val isRelease = details.isCleanTag && !details.lastTag.endsWith("-SNAPSHOT")

println("Preparing build ${project.name} $version")

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs = listOf("-Xjvm-default=compatibility")
}

repositories {
    jcenter()
    mavenCentral()
    maven { url = URI.create("https://hub.spigotmc.org/nexus/content/repositories/snapshots") }
    maven { url = URI.create("https://oss.sonatype.org/content/repositories/snapshots/") }
    maven { url = URI.create("https://oss.sonatype.org/content/repositories/releases/") }
    maven { url = URI.create("https://repo.codemc.org/repository/maven-public") }
    maven { url = URI.create("https://jitpack.io") }
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib")
    compileOnly(kotlin("reflect", kotlinVersion))

    implementation("org.bstats:bstats-bukkit:1.7") {
        isTransitive = false
    }

    compileOnly("io.reactivex.rxjava3:rxjava:3.0.11")
    compileOnly("io.reactivex.rxjava3:rxkotlin:3.0.1")
    compileOnly("org.reflections:reflections:0.9.12")

    compileOnly("com.google.code.gson:gson:2.8.6")
    compileOnly("org.yaml:snakeyaml:1.26")
    compileOnly("com.moandjiezana.toml:toml4j:0.7.2")

    compileOnly("info.picocli:picocli:4.3.2")

    compileOnly("org.apache.logging.log4j:log4j-core:2.12.1")

    compileOnly("com.squareup.retrofit2:retrofit:2.9.0")
    compileOnly("com.squareup.retrofit2:adapter-rxjava3:2.9.0")
    compileOnly("com.squareup.retrofit2:converter-gson:2.9.0")

    compileOnly("net.sourceforge.cssparser:cssparser:0.9.27")

    compileOnly("org.spigotmc:spigot-api:1.16.4-R0.1-SNAPSHOT")

    compileOnly(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation("com.github.clayclaw:XakuDependencyManager:3929620b6e")
}
val dokka = (tasks["dokka"] as DokkaTask).apply {
    outputFormat = "html"
}

val dokkaJavadoc by tasks.registering(DokkaTask::class) {
    outputFormat = "javadoc"
    outputDirectory = "$buildDir/javadoc"
}

gradle.taskGraph.whenReady {
    if (allTasks.any { it is Sign }) {
        allprojects {
            extra["signing.keyId"] = System.getenv("SIGNING_KEYID")
        }
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    dependsOn(JavaPlugin.CLASSES_TASK_NAME)
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val javadocJar by tasks.registering(Jar::class) {
    dependsOn(dokkaJavadoc)
    archiveClassifier.set("javadoc")
    from(tasks.javadoc)
}

val shadowJar = (tasks["shadowJar"] as ShadowJar).apply {
    relocate("org.bstats", "dev.reactant.reactant.core")
    // relocate("okhttp3", "dev.reactant.reactant.okhttp3")
    // relocate("okio", "dev.reactant.reactant.okio")
    // relocate("cssparser", "dev.reactant.reactant.cssparser")
    // relocate("javassist", "dev.reactant.reactant.javassist")
}

val dokkaJar by tasks.registering(Jar::class) {
    dependsOn(dokka)
    archiveClassifier.set("dokka")
    from(tasks.dokka)
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
            artifact(javadocJar.get())
            artifact(dokkaJar.get())
            artifact(shadowJar)

            groupId = group.toString()
            artifactId = project.name
            version = version

            pom {
                name.set("Reactant")
                description.set("An elegant plugin framework for spigot")
                url.set("https://reactant.dev")
                licenses {
                    license {
                        name.set("GPL-3.0")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.txt")
                    }
                }
                scm {
                    connection.set("scm:git:git@gitlab.com:reactant/reactant.git")
                    url.set("https://gitlab.com/reactant/reactant/")
                }

                developers {
                    developer {
                        id.set("setako")
                        name.set("Setako")
                        organization.set("Reactant Dev Team")
                        organizationUrl.set("https://gitlab.com/reactant")
                    }
                }
            }
        }
    }

    repositories {
        maven {

            val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
            val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            credentials {
                username = System.getenv("SONATYPE_USERNAME")
                password = System.getenv("SONATYPE_PASSWORD")
            }
        }
    }
}

if (isRelease) {
    signing {
        ext["signing.keyId"] = findProperty("signingKeyId") as String?
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey?.replace("\\n", "\n"), signingPassword)
        sign(publishing.publications["maven"])
    }
}
