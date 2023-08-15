package android.buiducha.translateapp.repository

import android.buiducha.translateapp.api.RetrofitInstance
import android.buiducha.translateapp.model.LanguageList
import android.buiducha.translateapp.model.Vocabulary
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Response

class TranslateRepository {
    suspend fun getTranslate(text: String, lang: String): Response<Vocabulary> {
        return RetrofitInstance.api.getTranslate(text, lang)
    }

    suspend fun getLangList(): Response<LanguageList> {
        return RetrofitInstance.api.getLangList()
    }
}