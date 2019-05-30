package net.swamphut.swampium.repository.config

class RepositoryConfig {
    val repositories: LinkedHashMap<String, String> = linkedMapOf(
            "mavenCentral" to "https://repo.maven.apache.org/maven2",
            "swamphut" to "https://dl.bintray.com/setako/swamphut"
    )
}