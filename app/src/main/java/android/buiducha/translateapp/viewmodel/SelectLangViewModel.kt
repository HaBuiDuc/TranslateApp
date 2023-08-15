package android.buiducha.translateapp.viewmodel

import android.buiducha.translateapp.util.Language
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.buiducha.translateapp.repository.LanguageDSRepository.readLang
import android.buiducha.translateapp.repository.LanguageDSRepository.saveLang
import android.buiducha.translateapp.repository.LanguageDSRepository.SOURCE_RECENT
import android.buiducha.translateapp.repository.LanguageDSRepository.DES_RECENT
import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SelectLangViewModel : ViewModel() {
    val language = Language
    private val _recentLang = MutableLiveData<List<String>?>()
    val recentLang: LiveData<List<String>?> get() = _recentLang

    fun getRecentLang(context: Context, target: Int) {
        if (target == 0) {
            viewModelScope.launch {
                _recentLang.value = readLang(context, SOURCE_RECENT)
            }
        } else {
            viewModelScope.launch {
                _recentLang.value = readLang(context, DES_RECENT)
            }
        }
        Log.d(TAG, "getRecentLang: ")
    }

    fun saveRecentLang(context: Context, data: String, target: Int) {
        if (target == 0) {
            viewModelScope.launch {
                saveLang(context, data, SOURCE_RECENT)
            }
        } else {
            viewModelScope.launch {
                saveLang(context, data, DES_RECENT)
            }
        }
        getRecentLang(context, target)
    }
    
    companion object {
        const val TAG = "SelectLanguageViewModel"
    }
}