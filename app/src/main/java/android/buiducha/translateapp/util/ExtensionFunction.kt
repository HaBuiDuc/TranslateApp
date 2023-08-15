package android.buiducha.translateapp.util

import android.buiducha.translateapp.model.Vocabulary
import android.widget.TextView
import java.util.*

fun TextView.clearText() {
    this.text = ""
}

fun Vocabulary.langCap(): String {
    return this.lang.split('-').joinToString(" - ") {lang ->
        lang.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    }
}

fun <T> MutableList<T>.swap(index1: Int, index2: Int){
    val tmp = this[index1]
    this[index1] = this[index2]
    this[index2] = tmp
}