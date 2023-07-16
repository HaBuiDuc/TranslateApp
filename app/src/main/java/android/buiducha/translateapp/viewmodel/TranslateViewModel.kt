package android.buiducha.translateapp.viewmodel

import android.buiducha.translateapp.model.Vocabulary
import android.buiducha.translateapp.repository.TranslateRepository
import android.buiducha.translateapp.util.Language
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

const val TAG = "TranslateViewModel"
class TranslateViewModel : ViewModel() {
    private val translateRepository = TranslateRepository()
    private val _vocabularyList = MutableLiveData<List<Vocabulary>>()
    val language = Language
    val vocabularyList: LiveData<List<Vocabulary>> get() = _vocabularyList

    init {
        // init vocabulary list value
        _vocabularyList.value = mutableListOf()
        Log.d(TAG, Language.codeToLang.size.toString())
    }

    // get translate from api
    fun getTranslate(text: String, lang: String) {
        viewModelScope.launch {
            val vocabulary = try {
                translateRepository.getTranslate(text, lang)
            } catch (e: IOException) {
                Log.e(TAG, "IOException")
                return@launch
            } catch (e: HttpException) {
                Log.e(TAG, "HTTPException")
                return@launch
            }
            if (vocabulary.isSuccessful && vocabulary.body() != null) {
                Log.d(TAG, "Successful")
                vocabulary.body()!!.sourceText = text
                // add value into vocabulary list
                _vocabularyList.value = _vocabularyList.value?.plus(vocabulary.body()!!)
            } else {
                Log.d(TAG, "Failure")
            }
        }
    }
}
