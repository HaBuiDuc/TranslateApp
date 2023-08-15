package android.buiducha.translateapp

import android.app.Application
import android.buiducha.translateapp.repository.FavoriteRepository

class TranslateApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FavoriteRepository.initialize(this)
    }
}