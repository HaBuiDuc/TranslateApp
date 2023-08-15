package android.buiducha.translateapp.util

import java.util.Locale

fun getLanguageCode(language: String) =
    Locale.getISOLanguages().find {
        Locale(it).displayLanguage == language
    }