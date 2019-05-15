package net.swamphut.swampium.example

import java.util.*

class TesterList {
    var testers: List<Tester> = ArrayList()

    inner class Tester {
        var name: String? = null
        var age: Int = 0
        var address: String? = null
        var favouriteFoods: List<String>? = null
    }
}
