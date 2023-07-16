package android.buiducha.translateapp.fragment

import android.annotation.SuppressLint
import android.buiducha.translateapp.R
import android.buiducha.translateapp.databinding.TranslateFragmentBinding
import android.buiducha.translateapp.repository.LanguageDSRepository.DES_RECENT
import android.buiducha.translateapp.repository.LanguageDSRepository.SOURCE_RECENT
import android.buiducha.translateapp.repository.LanguageDSRepository.saveLang
import android.buiducha.translateapp.viewmodel.TranslateViewModel
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.launch


@Suppress("UNCHECKED_CAST")
class TranslateFragment : Fragment() {
    private lateinit var viewBinding: TranslateFragmentBinding
    private val viewModel: TranslateViewModel by viewModels()
    private var isClicked = false

    private val voiceFbAppearAnim by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.voice_fb_appear_anim)
    }
    private val kbFbAppearAnim by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.kb_fb_appear_anim)
    }
    private val imgFbAppearAnim by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.img_fb_appear_anim)
    }
    private val voiceFbDisappearAnim by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.voice_fb_disappear_anim)
    }
    private val kbFbDisappearAnim by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.kb_fb_disappear_anim)
    }
    private val imgFbDisappearAnim by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.img_fb_disappear_anim)
    }
    private val translateOpenAnim by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.translate_fb_open_anim)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewBinding = TranslateFragmentBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // call hide soft keyboard function
        setupUI(view)
        viewBinding.apply {

            viewPagerSetup()

            // set language value
            sourceLanguage.text = viewModel.language.langList[0]
            sourceLanguage.setOnClickListener {
                val languageSelect = newSelectLanguage()
                languageSelect.target = 0
                languageSelect.show(parentFragmentManager, SelectLanguageBottomSheet.TAG)
            }

            destinationLanguage.text = viewModel.language.langList[1]
            destinationLanguage.setOnClickListener {
                val languageSelect = newSelectLanguage()
                languageSelect.target = 1
                languageSelect.show(parentFragmentManager, SelectLanguageBottomSheet.TAG)
            }

            // implement language swap button
            languageSwap.apply {
                setOnClickListener {
                    val tmp = destinationLanguage.text
                    destinationLanguage.text = sourceLanguage.text
                    sourceLanguage.text = tmp
                    viewLifecycleOwner.lifecycleScope.launch {
                        saveLang(requireContext(), tmp.toString(), SOURCE_RECENT)
                        saveLang(requireContext(), destinationLanguage.text.toString(), DES_RECENT)
//                        Log.d(TAG, readLang(requireContext(), SOURCE_RECENT).toString())
                    }
                }
            }

            fbSetup()

            // implement send button
//            sendButton.setOnClickListener {
//                // get target and source language code
//                var target = destinationLanguage.text
//                var source = sourceLanguage.text
//                val text = inputWordEditText.text.toString()
//                inputWordEditText.setText("")
//                // get target and source language name
//                target = viewModel.language.langToCode[target].toString()
//                source = viewModel.language.langToCode[source].toString()
//                // parse value
//                val lang = "$source-$target"
//                // get api
//                if (text != "") {
//                    viewModel.getTranslate(text, lang)
//                }
//            }
        }
   }

    private fun fbSetup() {
        translateFbSetup()
        voiceInputFbSetup()
        kbInputFbSetup()
        imgInputFbSetup()
    }

    private fun translateFbSetup() {
        viewBinding.apply {
            translateFb.setOnClickListener {
                setVisibility()
                setAnimation()
                isClicked = !isClicked
            }
        }
    }



    private fun voiceInputFbSetup() {

    }

    private fun kbInputFbSetup() {
        viewBinding.apply {
            kbInputFb.setOnClickListener {

            }
        }
    }

    private fun imgInputFbSetup() {

    }

    private fun setVisibility() {
        viewBinding.apply {
            if (!isClicked) {
                voiceInputFb.visibility = View.VISIBLE
                imgInputFb.visibility = View.VISIBLE
                kbInputFb.visibility = View.VISIBLE
            } else {
                voiceInputFb.visibility = View.GONE
                imgInputFb.visibility = View.GONE
                kbInputFb.visibility = View.GONE
            }
        }
    }

    private fun setAnimation() {
        viewBinding.apply {
            if (!isClicked) {
                voiceInputFb.startAnimation(voiceFbAppearAnim)
                imgInputFb.startAnimation(imgFbAppearAnim)
                kbInputFb.startAnimation(kbFbAppearAnim)
//                translateFb.startAnimation(translateOpenAnim)
            } else {
                voiceInputFb.startAnimation(voiceFbDisappearAnim)
                imgInputFb.startAnimation(imgFbDisappearAnim)
                kbInputFb.startAnimation(kbFbDisappearAnim)
            }
        }
    }

    private fun viewPagerSetup() {
        viewBinding.translateVp.orientation = ViewPager2.ORIENTATION_VERTICAL
//        viewModel.vocabularyList.observe(viewLifecycleOwner) { vocabList ->
//            viewBinding.translateVp.adapter = object : FragmentStateAdapter(requireActivity()) {
//                override fun getItemCount(): Int = vocabList.size
//
//                override fun createFragment(position: Int): Fragment {
//                    return TranslateVPFragment(vocabList[position])
//                }
//            }
//        }
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
//        viewBinding.inputWordEditText.clearFocus()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setupUI(view: View) {

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
        const val TAG = "TranslateFragment"
    }
}