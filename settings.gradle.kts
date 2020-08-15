pluginManagement {
    repositories {
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
}
include(":reactant-rui")
project(":reactant-rui").projectDir = File(rootDir, "reactant-rui")
rootProject.name = "reactant"
