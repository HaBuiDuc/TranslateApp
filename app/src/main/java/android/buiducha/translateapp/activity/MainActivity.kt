package android.buiducha.translateapp.activity

import android.buiducha.translateapp.fragment.TranslateFragment
import android.buiducha.translateapp.R
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }

        const val TAG = "MainActivity"
        const val LANGUAGE_LIST_EXTRA = "language_list_extra"
    }
}