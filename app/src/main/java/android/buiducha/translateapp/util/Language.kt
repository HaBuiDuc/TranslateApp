package android.buiducha.translateapp.util

object Language {
    var codeToLang = HashMap<String, String>()
    val langToCode by lazy { codeToLang.map { (key, value) ->
        value to key
    }.toMap() as HashMap<String, String> }
    val langList by lazy {
        langToCode.keys.toMutableList().sorted()
    }
}