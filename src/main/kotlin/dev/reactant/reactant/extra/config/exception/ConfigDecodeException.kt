package dev.reactant.reactant.extra.config.exception

import dev.reactant.reactant.utils.stackTraceString

class ConfigDecodeException(
        pathExceptionMap: Map<String, Throwable>
) : RuntimeException("Error occurred when decoding config:\n ${pathExceptionMap.entries.map { "${it.key}: ${it.value.stackTraceString}" }}") {

}
