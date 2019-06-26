package io.reactant.reactant.repository.config

class RepositoryConfig {

    /**
     * True to refuse player execute the command
     */
    var consoleOnly = true

    var repositories: LinkedHashMap<String, String> = linkedMapOf(
            "mavenCentral" to "https://repo.maven.apache.org/maven2",
            "reactant" to "https://dl.bintray.com/reactant/reactant"
    )
}
