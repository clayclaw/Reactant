package dev.reactant.reactant.extra.i18n

import java.util.*

class SimulatedResourceBundle(languageTables: Set<LanguageTableClass>) : ResourceBundle() {
    private lateinit var keys: Enumeration<String>;
    override fun getKeys() = keys

    init {
//        keys = languageTables.map { it.declaredMemberProperties }
    }

    override fun getLocale(): Locale {
        return super.getLocale()
    }

    override fun keySet(): MutableSet<String> {
        return super.keySet()
    }

    override fun containsKey(p0: String): Boolean {
        return super.containsKey(p0)
    }

    override fun getBaseBundleName(): String {
        return super.getBaseBundleName()
    }

    override fun handleGetObject(p0: String): Any {
        throw UnsupportedOperationException()
    }
}
