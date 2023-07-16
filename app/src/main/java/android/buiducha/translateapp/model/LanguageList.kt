package android.buiducha.translateapp.model

import com.google.gson.annotations.SerializedName

data class LanguageList(
    val dirs: List<String>,
    @SerializedName("langs")
    val langList: HashMap<String, String>
)