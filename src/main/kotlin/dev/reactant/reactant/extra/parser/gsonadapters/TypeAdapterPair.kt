package dev.reactant.reactant.extra.parser.gsonadapters

import java.lang.reflect.Type

interface TypeAdapterPair {
    val type: Type;

    /**
     * Type adapter that will register into GSON
     * @see GsonBuilder.registerTypeAdapter
     */
    val typeAdapter: Any
}
