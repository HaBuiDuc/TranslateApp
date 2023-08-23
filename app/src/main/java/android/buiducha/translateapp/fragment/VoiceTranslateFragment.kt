package android.buiducha.translateapp.fragment

import android.annotation.SuppressLint
import android.buiducha.translateapp.databinding.VoiceTranslateFragmentBinding
import android.buiducha.translateapp.repository.LanguageDSRepository
import android.buiducha.translateapp.util.checkAudioRecordPermission
import android.buiducha.translateapp.util.copyToClipboard
import android.buiducha.translateapp.util.textToSpeechCreate
import android.buiducha.translateapp.viewmodel.TranslateViewModel
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.*

class VoiceTranslateFragment : Fragment() {
    private lateinit var viewBinding: VoiceTranslateFragmentBinding
    private val viewModel: TranslateViewModel by viewModels()
    private lateinit var textToSpeech: TextToSpeech

    private val speechRecognizer by lazy {
        SpeechRecognizer.createSpeechRecognizer(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewBinding = VoiceTranslateFragmentBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recordButtonSetup()
        Log.d(TAG, Locale.getDefault().toString())
        speechRecognizerSetup()

        languageSelectSetup()
        languageSwapSetup()

        meaningTVSetup()

        textToSpeechSetup()

        speakButtonSetup()

        copyButtonSetup()

        wordTvSetup()
    }

    private fun copyButtonSetup() {
        viewBinding.meaningCp.setOnClickListener {
            val data = viewBinding.meaningTv.text.toString()
            copyToClipboard(requireContext(), data)
        }
    }

    private fun speakButtonSetup() {
        fun speak(text: String, textToSpeech: TextToSpeech) {
            lifecycleScope.launch {
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }

        viewBinding.apply {
            meaningSpeaker.setOnClickListener {
                val result = textToSpeech.isLanguageAvailable(Locale.ENGLISH)
                if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.d(TAG, "not support")
                    startActivity(Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA))
                }
                val text = meaningTv.text.toString()
                speak(text, textToSpeech)
            }
        }
    }

    private fun textToSpeechSetup() {
        textToSpeech = textToSpeechCreate(requireContext())
        lifecycleScope.launch {
            viewModel.languagePair.collect {langPair ->
                val target = viewModel.language.langToCode[langPair[1]]
                target?.let {
                    textToSpeech.language = Locale(target)
                }
            }
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun recordButtonSetup() {
        viewBinding.recordBt.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                Log.d(TAG, "Pressed")
                var lang = viewBinding.sourceLanguage.text.toString()
                lang = viewModel.language.langToCode[lang].toString()
                viewBinding.pulsator.start()
                startListening(lang)
            } else if (event.action == MotionEvent.ACTION_UP) {
                Log.d(TAG, "release")
                viewBinding.pulsator.stop()
                stopListening()
            }
            true
        }

    }

//    private fun audioVisualizerSetup() {
//        viewBinding.audioVisualizer.apply {
//            visibility = View.VISIBLE
//            setColor(R.color.black)
//            setDensity(60F)
//
//        }
//    }

    private fun speechRecognizerSetup() {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p0: Bundle?) {
            }

            override fun onBeginningOfSpeech() {
            }

            override fun onRmsChanged(p0: Float) {
            }

            override fun onBufferReceived(p0: ByteArray?) {
            }

            override fun onEndOfSpeech() {
                Log.d(TAG, "onEndOfSpeech: ")
            }

            override fun onError(p0: Int) {
            }

            override fun onResults(p0: Bundle?) {
                if (p0 != null) {
                    setResult(p0)
                    val result = p0.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.joinToString()
                    viewModel.getTranslate(result!!)
                } else {
                    Log.d(TAG, "onResults: null")
                }
            }

            override fun onPartialResults(p0: Bundle?) {
                if (p0 != null) {
                    setResult(p0)
                }
            }

            override fun onEvent(p0: Int, p1: Bundle?) {
            }

        })
    }
    private fun setResult(bundle: Bundle) {
        val result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (result != null) {
            viewBinding.wordTv.text = result.joinToString()
        }
    }

    private fun startListening(lang: String) {
        checkAudioRecordPermission(requireActivity())
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang)
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        speechRecognizer.startListening(intent)
    }

    private fun stopListening() {
        speechRecognizer.stopListening()
        Log.d(TAG, "stopListening: ")
    }


    private fun languageSelectSetup() {
        viewBinding.apply {
            lifecycleScope.launch {
                val recentSource = LanguageDSRepository.readLang(requireContext(), LanguageDSRepository.SOURCE_RECENT)
                val recentTarget = LanguageDSRepository.readLang(requireContext(), LanguageDSRepository.DES_RECENT)

                if (recentSource.size != 0) {
                    sourceLanguage.text = recentSource.last()
                } else {
                    sourceLanguage.text = viewModel.language.langList[0]
                }
                if (recentTarget.size != 0) {
                    destinationLanguage.text = recentTarget.last()
                } else {
                    destinationLanguage.text = viewModel.language.langList[1]
                }
            }
            sourceLanguage.setOnClickListener {
                val languageSelect = newSelectLanguage()
                languageSelect.target = 0
                languageSelect.show(parentFragmentManager, SelectLanguageBottomSheet.TAG)
            }
            sourceLanguage.addTextChangedListener {
                viewModel.sourceLangUpdate(it.toString())
            }

            destinationLanguage.setOnClickListener {
                val languageSelect = newSelectLanguage()
                languageSelect.target = 1
                languageSelect.show(parentFragmentManager, SelectLanguageBottomSheet.TAG)
            }
            destinationLanguage.addTextChangedListener {
                viewModel.targetLangUpdate(it.toString())
            }
        }
    }
    private fun languageSwapSetup() {
        viewBinding.apply {
            languageSwap.apply {
                setOnClickListener {
                    val tmp = destinationLanguage.text
                    destinationLanguage.text = sourceLanguage.text
                    sourceLanguage.text = tmp
                    viewLifecycleOwner.lifecycleScope.launch {
                        LanguageDSRepository.savePairLang(
                            requireContext(),
                            tmp.toString(),
                            destinationLanguage.toString()
                        )
                    }
                }
            }
        }
    }

    private fun newSelectLanguage() = SelectLanguageBottomSheet(
        viewBinding.sourceLanguage.text.toString(),
        viewBinding.destinationLanguage.text.toString()
    ) {language, target ->
        if (target == 0) {
            viewBinding.sourceLanguage.text = language
            viewLifecycleOwner.lifecycleScope.launch {
                LanguageDSRepository.saveLang(requireContext(), language, LanguageDSRepository.SOURCE_RECENT)
            }
        } else {
            viewBinding.destinationLanguage.text = language
            viewLifecycleOwner.lifecycleScope.launch {
                LanguageDSRepository.saveLang(requireContext(), language, LanguageDSRepository.DES_RECENT)
            }
        }
    }

    private fun meaningTVSetup() {
        viewBinding.meaningTv.movementMethod = ScrollingMovementMethod()
        lifecycleScope.launch {
            viewModel.vocabulary.collect {vocab ->
                if (vocab != null) {
                    viewBinding.meaningTv.text = vocab.text.joinToString()
                }
            }
        }
    }

    private fun wordTvSetup() {
        viewBinding.wordTv.movementMethod = ScrollingMovementMethod()
    }

    companion object {
        const val TAG = "VoiceTranslateFragment"
    }
}