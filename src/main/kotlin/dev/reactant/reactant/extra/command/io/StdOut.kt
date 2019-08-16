package dev.reactant.reactant.extra.command.io

interface StdOut {
    var consumer: StdOutConsumer;
    fun out(output: String)
}
