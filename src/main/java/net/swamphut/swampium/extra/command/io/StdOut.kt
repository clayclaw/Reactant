package net.swamphut.swampium.extra.command.io

interface StdOut {
    var consumer: StdOutConsumer;
    fun out(output: String)
}
