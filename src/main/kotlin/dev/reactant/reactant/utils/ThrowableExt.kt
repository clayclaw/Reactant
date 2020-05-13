package dev.reactant.reactant.utils

import java.io.PrintWriter
import java.io.StringWriter

val Throwable.stackTraceString: String
    get() =
        StringWriter().apply { this@stackTraceString.printStackTrace(PrintWriter(this)) }.toString()
