package android.buiducha.translateapp.api

import android.buiducha.translateapp.model.LanguageList
import android.buiducha.translateapp.model.Vocabulary
import android.buiducha.translateapp.util.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface YandexTranslateApi {
    @GET("translate")
    suspend fun getTranslate(
        @Query("text") text: String,
        @Query("lang") lang: String,
        @Query("format") format: String = "plain",
        @Query("key") key: String = API_KEY
    ): Response<Vocabulary>

    @GET("getLangs")
    suspend fun getLangList(
        @Query("key") key: String = API_KEY,
        @Query("ui") ui: String = "en"
    ): Response<LanguageList>
}