package android.buiducha.translateapp

import android.app.Application
import android.buiducha.translateapp.fragment.TextTranslateFragment
import android.buiducha.translateapp.repository.FavoriteRepository
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import com.google.mlkit.common.MlKit

class TranslateApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FavoriteRepository.initialize(this)
        MlKit.initialize(this)
    }
}