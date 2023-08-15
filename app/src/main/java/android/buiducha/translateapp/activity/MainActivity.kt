package android.buiducha.translateapp.activity

import android.buiducha.translateapp.R
import android.buiducha.translateapp.databinding.ActivityMainBinding
import android.buiducha.translateapp.fragment.FavoriteVocabFragment
import android.buiducha.translateapp.fragment.TextTranslateFragment
import android.buiducha.translateapp.fragment.VoiceTranslateFragment
import android.buiducha.translateapp.repository.LanguageDSRepository
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    private val voiceFragment = VoiceTranslateFragment()
//    private val textFragment = TextTranslateFragment()
//    private val favFragment = FavoriteVocabFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        loadFragment(voiceFragment)
        viewBinding.translateBottomNav.selectedItemId = R.id.voice_input
        bottomNavSetup()
    }

    private fun langTest() {
        lifecycleScope.launch {
            val recentList = LanguageDSRepository.readLang(this@MainActivity, LanguageDSRepository.SOURCE_RECENT)
            Log.d(TAG, recentList.toString())
        }
    }
    private fun bottomNavSetup() {
        viewBinding.translateBottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.voice_input -> {
                    langTest()
                    val voiceFragment = VoiceTranslateFragment()
                    loadFragment(voiceFragment)
                }
                R.id.kb_input -> {
                    langTest()
                    val textFragment = TextTranslateFragment()
                    loadFragment(textFragment)
                }
                R.id.img_input -> {
                }
                R.id.word_favorite -> {
                    langTest()
                    val favFragment = FavoriteVocabFragment()
                    loadFragment(favFragment)
                }
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }

        const val TAG = "MainActivity"
        const val LANGUAGE_LIST_EXTRA = "language_list_extra"
    }
}