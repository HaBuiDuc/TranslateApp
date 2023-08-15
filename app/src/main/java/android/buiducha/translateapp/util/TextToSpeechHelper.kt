package android.buiducha.translateapp.util

import android.buiducha.translateapp.fragment.TextTranslateFragment
import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log

fun textToSpeechCreate(context: Context) =
    TextToSpeech(context) {
        if (it == TextToSpeech.SUCCESS) {
            Log.d(TextTranslateFragment.TAG, "Text to speech create successfully")
        } else if (it == TextToSpeech.ERROR) {
            Log.e(TextTranslateFragment.TAG, "Text to speech create failure")
        }
    }