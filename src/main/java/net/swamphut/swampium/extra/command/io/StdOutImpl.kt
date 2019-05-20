package net.swamphut.swampium.extra.command.io

class StdOutImpl(override var consumer: StdOutConsumer) : StdOut {

    override fun out(output: String) {
        consumer.onOut(output)
    }
}
