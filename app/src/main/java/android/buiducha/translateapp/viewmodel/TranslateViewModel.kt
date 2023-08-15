package android.buiducha.translateapp.viewmodel

import android.buiducha.translateapp.model.Vocabulary
import android.buiducha.translateapp.repository.TranslateRepository
import android.buiducha.translateapp.util.Language
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

const val TAG = "TranslateViewModel"
class TranslateViewModel : ViewModel() {
    val language = Language
    private val translateRepository = TranslateRepository()
    private val _vocabulary = MutableStateFlow<Vocabulary?>(null)
    val vocabulary: StateFlow<Vocabulary?> get() = _vocabulary
    private val _languagePair = MutableStateFlow(arrayOf("English", "Vietnamese"))
    val languagePair: StateFlow<Array<String>> get() = _languagePair
    private var job: Job? = null

    // get translate from api
    fun getTranslate(text: String) {
        val lang = parseLang(languagePair.value[0], languagePair.value[1])
        job = viewModelScope.launch {
            val result = try {
                translateRepository.getTranslate(text, lang)
            } catch (e: HttpException) {
                Log.e(TAG, "Can't fetch result", e)
                return@launch
            }
            if (result.isSuccessful && result.body() != null) {
                result.body()!!.sourceText = text
                _vocabulary.value = result.body()
            }
        }
    }

    private fun parseLang(source: String, target: String): String {
        val newSource = language.langToCode[source]
        val newTarget = language.langToCode[target]
        return "$newSource-$newTarget"
    }

    fun sourceLangUpdate(lang: String) {
        val langPair = _languagePair.value.copyOf()
        langPair[0] = lang
        _languagePair.value = langPair
    }

    fun targetLangUpdate(lang: String) {
        val langPair = _languagePair.value.copyOf()
        langPair[1] = lang
        _languagePair.value = langPair
    }

    fun cancelTranslate() {
        job?.cancel()
    }
}
