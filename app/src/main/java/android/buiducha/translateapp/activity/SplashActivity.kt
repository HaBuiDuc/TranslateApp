package android.buiducha.translateapp.activity

import android.annotation.SuppressLint
import android.buiducha.translateapp.repository.TranslateRepository
import android.buiducha.translateapp.R
import android.buiducha.translateapp.util.Language
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val translateRepository = TranslateRepository()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_layout)
        supportActionBar?.hide()
    }

    override fun onStart() {
        super.onStart()

        lifecycleScope.launch {
            val langList = try {
                translateRepository.getLangList()
            } catch (e: IOException) {
                Log.e(TAG, "IOException", e)
                return@launch
            } catch (e: HttpException) {
                Log.e(TAG, "HTTPException", e)
                return@launch
            }

            if (langList.isSuccessful && langList.body() != null) {
                Log.d(TAG, langList.body()!!.langList.size.toString())
                Language.codeToLang = langList.body()!!.langList
                val intent = MainActivity.newIntent(this@SplashActivity)
                startActivity(intent)
                finish()
            } else {
                Log.d(TAG, "Get lang list failure")
            }
        }
    }

    companion object {
        const val TAG = "SplashActivity"
    }
}