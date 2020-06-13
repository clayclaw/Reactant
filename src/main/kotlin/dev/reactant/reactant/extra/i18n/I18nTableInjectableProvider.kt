package dev.reactant.reactant.extra.i18n

import dev.reactant.reactant.core.ReactantCore
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.injection.Inject
import dev.reactant.reactant.core.dependency.injection.ProvideSubtype
import dev.reactant.reactant.service.spec.config.Config
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

@Component
internal class I18nTableInjectableProvider(
        private val i18nService: I18nService,
        @Inject("${ReactantCore.configDirPath}/i18n/config.json") private val languageConfig: Config<GlobalI18nConfig>
) {
    @ProvideSubtype(".*")
    private fun getI18nTable(kType: KType, languageCode: String): I18nTable {
        @Suppress("UNCHECKED_CAST")
        val tableClass = kType.jvmErasure as KClass<out I18nTable>
        return i18nService.getLanguage(tableClass,
                if (languageCode == "") languageConfig.content.languages
                else languageCode.split(",").map { it.trim() }
        ).blockingGet()
    }
}
