package android.buiducha.translateapp.api

import android.buiducha.translateapp.model.LanguageList
import android.buiducha.translateapp.model.Vocabulary
import android.buiducha.translateapp.util.Constants.API_KEY
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface YandexTranslateApi {
    @GET("translate?format=plain&key=$API_KEY")
    suspend fun getTranslate(
        @Query("text") text: String,
        @Query("lang") lang: String,
    ): Response<Vocabulary>

    @GET("getLangs")
    suspend fun getLangList(
        @Query("key") key: String = API_KEY,
        @Query("ui") ui: String = "en"
    ): Response<LanguageList>

}