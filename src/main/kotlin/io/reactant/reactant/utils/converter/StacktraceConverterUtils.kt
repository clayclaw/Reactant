package io.reactant.reactant.utils.converter

import java.io.PrintWriter
import java.io.StringWriter

class StacktraceConverterUtils {
    companion object {
        fun convertToString(e: Throwable): String =
                StringWriter().also { e.printStackTrace(PrintWriter(it)) }.toString()
    }
}