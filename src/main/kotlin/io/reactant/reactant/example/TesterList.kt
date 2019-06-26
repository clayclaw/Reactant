package io.reactant.reactant.example

class TesterList {
    var testers: ArrayList<io.reactant.reactant.example.TesterList.Tester> = ArrayList()

    class Tester {
        var name: String? = null
        var age: Int = 0
        var address: String? = null
        var favouriteFoods: List<String>? = null
    }
}
