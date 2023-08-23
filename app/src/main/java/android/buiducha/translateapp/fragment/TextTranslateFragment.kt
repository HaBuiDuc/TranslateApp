package android.buiducha.translateapp.fragment

import android.annotation.SuppressLint
import android.buiducha.translateapp.databinding.TextTranslateFragmentBinding
import android.buiducha.translateapp.repository.FavoriteRepository
import android.buiducha.translateapp.repository.LanguageDSRepository.DES_RECENT
import android.buiducha.translateapp.repository.LanguageDSRepository.SOURCE_RECENT
import android.buiducha.translateapp.repository.LanguageDSRepository.readLang
import android.buiducha.translateapp.repository.LanguageDSRepository.saveLang
import android.buiducha.translateapp.util.clearText
import android.buiducha.translateapp.util.copyToClipboard
import android.buiducha.translateapp.util.textToSpeechCreate
import android.buiducha.translateapp.viewmodel.TranslateViewModel
import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class TextTranslateFragment : Fragment() {
    private lateinit var viewBinding: TextTranslateFragmentBinding
    private val viewModel: TranslateViewModel by viewModels()
    private lateinit var textToSpeechMeaning: TextToSpeech
    private lateinit var textToSpeechWord: TextToSpeech
    private val favoriteRepository = FavoriteRepository.get()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewBinding = TextTranslateFragmentBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // call hide soft keyboard function
        setupUI(view)

        languageSelectSetup()
        // implement language swap button
        languageSwapSetup()

        onTranslateListener()

        copyButtonSetup()

        wordFvButtonSetup()

        textToSpeechSetup()

        speakButtonSetup()

        meaningTvSetup()
   }

    private fun speakButtonSetup() {
        fun speak(text: String, textToSpeech: TextToSpeech) {
            lifecycleScope.launch(Dispatchers.Unconfined) {
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }

        viewBinding.apply {
            wordSpeaker.setOnClickListener {
                val text = wordEt.text.toString()
                speak(text, textToSpeechWord)
            }

            meaningSpeaker.setOnClickListener {
                val text = meaningTv.text.toString()
                speak(text, textToSpeechMeaning)
            }
        }
    }

    private fun wordFvButtonSetup() {
        viewBinding.wordFavorite.setOnClickListener { view ->
            view.isSelected = !view.isSelected
            val item = viewModel.vocabulary.value
            viewLifecycleOwner.lifecycleScope.launch {
                item?.let {vocab ->
                    if (view.isSelected) {
                        vocab.id = UUID.randomUUID()
                        favoriteRepository.addVocabulary(vocab)
                    } else {
                        favoriteRepository.deleteVocabulary(vocab)
                    }
                }
            }
        }
    }

    private fun textToSpeechSetup() {
        textToSpeechWord = textToSpeechCreate(requireContext())
        textToSpeechMeaning = textToSpeechCreate(requireContext())
        lifecycleScope.launch {
            viewModel.languagePair.collect {langPair ->
                val source = viewModel.language.langToCode[langPair[0]]
                val target = viewModel.language.langToCode[langPair[1]]
                source?.let {
                    textToSpeechWord.language = Locale(source)
                }
                target?.let {
                    textToSpeechMeaning.language = Locale(target)
                }
            }
        }
    }

    private fun copyButtonSetup() {
        viewBinding.meaningCp.setOnClickListener {
            val data = viewBinding.meaningTv.text.toString()
            copyToClipboard(requireContext(), data)
        }
    }

    private fun meaningTvSetup() {
        viewBinding.meaningTv.movementMethod = ScrollingMovementMethod()
    }

    private fun onTranslateListener() {
        viewBinding.apply {

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.vocabulary.collect {vocab ->
                    if (vocab != null) {
                        meaningTv.text = vocab.text.joinToString()
                    }
                }
            }

            wordEt.addTextChangedListener {
                lifecycleScope.launch {
                    getTranslate()
                }
            }
        }
    }

    private fun getTranslate() {
        viewBinding.apply {
            val queryText = wordEt.text.toString()
            if (queryText != "") {
                viewModel.cancelTranslate()
                // parse value
                viewModel.getTranslate(queryText)
                Log.d(TAG, "translate: $queryText ")
            } else {
                viewModel.cancelTranslate()
                meaningTv.clearText()
            }
        }
    }

    private fun languageSelectSetup() {
        viewBinding.apply {
            lifecycleScope.launch {
                val recentSource = readLang(requireContext(), SOURCE_RECENT)
                val recentTarget = readLang(requireContext(), DES_RECENT)

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

            destinationLanguage.addTextChangedListener {
                viewModel.targetLangUpdate(it.toString())
            }
            destinationLanguage.setOnClickListener {
                val languageSelect = newSelectLanguage()
                languageSelect.target = 1
                languageSelect.show(parentFragmentManager, SelectLanguageBottomSheet.TAG)
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
                        saveLang(requireContext(), tmp.toString(), SOURCE_RECENT)
                        saveLang(requireContext(), destinationLanguage.text.toString(), DES_RECENT)
                        getTranslate()
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
                saveLang(requireContext(),language, SOURCE_RECENT)
            }
        } else {
            viewBinding.destinationLanguage.text = language
            viewLifecycleOwner.lifecycleScope.launch {
                saveLang(requireContext(), language, DES_RECENT)
            }
        }
    }

    // use imm to hide soft keyboard when press outside edittext and clear edittext focus
    private fun hideKeyboard(view: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        viewBinding.wordEt.clearFocus()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUI(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { _, _ ->
                hideKeyboard(view)
                false
            }
        }
        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }

    companion object {
        const val TAG = "TextTranslateFragment"
    }
}