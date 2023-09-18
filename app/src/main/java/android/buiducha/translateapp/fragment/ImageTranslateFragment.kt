package android.buiducha.translateapp.fragment

import android.buiducha.translateapp.BuildConfig
import android.buiducha.translateapp.R
import android.buiducha.translateapp.databinding.ImageTranslateFragmentBinding
import android.buiducha.translateapp.repository.FavoriteRepository
import android.buiducha.translateapp.repository.LanguageDSRepository
import android.buiducha.translateapp.util.checkCameraPermission
import android.buiducha.translateapp.util.copyToClipboard
import android.buiducha.translateapp.util.textToSpeechCreate
import android.buiducha.translateapp.viewmodel.TranslateViewModel
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.datastore.core.IOException
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


class ImageTranslateFragment : Fragment() {
    private lateinit var viewBinding: ImageTranslateFragmentBinding
    private lateinit var textToSpeechMeaning: TextToSpeech
    private lateinit var textToSpeechWord: TextToSpeech
    private val favoriteRepository = FavoriteRepository.get()

    private val viewModel: TranslateViewModel by viewModels()

    private val textRecognition by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    private var methodSelect = true
    private val photoUri: Uri by lazy {
        getPhotoUri(requireContext())
    }

    private val photoIcon by lazy {
        ContextCompat.getDrawable(
            requireContext(),
            R.drawable.icon_photograph_solid
        )
    }
    private val cameraIcon by lazy {
        ContextCompat.getDrawable(
            requireContext(),
            R.drawable.icon_camera_solid
        )
    }
    private val cancelIcon by lazy {
        ContextCompat.getDrawable(
            requireContext(),
            R.drawable.icon_cancel
        )
    }
    private val photoButtonAppearAnim by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.photobt_appear_anim
        )
    }

    private val photoButtonDisappearAnim by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.photobt_disappear_anim
        )
    }

    private val cameraButtonAppearAnim by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.camerabt_appear_anim
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewBinding = ImageTranslateFragmentBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.apply {
            selectImageContainerSetup()
        }

        languageSelectSetup()
        languageSwapSetup()
        meaningTvSetup()
        wordTvSetup()
        copyButtonSetup()
        textToSpeechSetup()
        wordFvButtonSetup()
        speakButtonSetup()
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

    private fun selectImageContainerSetup() {
        animationSetup()
        buttonGroupSetup()
    }

    private fun buttonGroupSetup() {
        mainButtonSetup()
        cameraButtonSetup()
        photoButtonSetup()
    }

    private fun photoButtonSetup() {
        viewBinding.apply {
            photoBt.setOnClickListener {
                methodSelect = false
                groupButtonCollapse()
            }
        }
    }

    private fun cameraButtonSetup() {
        viewBinding.apply {
            cameraBt.setOnClickListener {
                methodSelect = true
                groupButtonCollapse()
            }
        }
    }

    private fun mainButtonSetup() {
        viewBinding.apply {
            mainBt.setOnLongClickListener {
                if (cameraBt.visibility == View.GONE) {
                    groupButtonExpand()
                }
                true
            }
            mainBt.setOnClickListener {
                if (mainBt.isSelected) {
                    groupButtonCollapse()
                } else {
                    if (methodSelect) {
                        getTranslateByCamera()
                    } else {
                        getTranslateByPhoto()
                    }
                }
            }
        }
    }

    private fun recognizeText(imageUri: Uri) {
        try {
            val inputImage = InputImage.fromFilePath(requireContext(), imageUri)
            lifecycleScope.launch {
                textRecognition.process(inputImage).addOnSuccessListener {text ->
                    Log.d("This is a log", text.text.replace("\n"," "))
                    val newText = text.text.replace("\n"," ")
                    viewModel.getTranslate(newText)
                    viewBinding.wordEt.text = newText
                }.addOnFailureListener {
                    it.printStackTrace()
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // Use the returned uri.
            val uriContent = result.uriContent
            result.getUriFilePath(requireContext()) // optional usage
            if (uriContent != null) {
                recognizeText(uriContent)
            }
        } else {
            // An error occurred.
            val exception = result.error
        }
    }

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto: Boolean ->
        // Handle the result
        if (didTakePhoto) {
            val cropImageOptions = CropImageOptions()
            cropImageOptions.imageSourceIncludeGallery = true
            cropImageOptions.imageSourceIncludeCamera = true
            val cropImageContractOptions = CropImageContractOptions(photoUri, cropImageOptions)
            cropImage.launch(cropImageContractOptions)
        }
    }

    private val pickPhoto = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {uri ->
        val cropImageOptions = CropImageOptions()
        cropImageOptions.imageSourceIncludeGallery = false
        cropImageOptions.imageSourceIncludeCamera = false
        val cropImageContractOptions = CropImageContractOptions(uri, cropImageOptions)
        cropImage.launch(cropImageContractOptions)
    }



    private fun getPhotoUri(context: Context): Uri {
        val photoName = "IMG_${Date()}.JPG"
        val photoFile = File (
                context.applicationContext.filesDir,
                photoName
        )
        return FileProvider.getUriForFile(
            requireContext(),
            BuildConfig.APPLICATION_ID + ".provider",
            photoFile
        )
    }

    private fun startCrop(option: String) {
        if (option == CAMERA_OPTION) {
            takePhoto.launch(photoUri)
        } else if (option == PICK_PHOTO_OPTION) {
            pickPhoto.launch("image/*")
        }
    }

    private fun getTranslateByCamera() {
        Toast.makeText(requireContext(), "camera select", Toast.LENGTH_SHORT).show()
        if (checkCameraPermission(requireActivity())) {
            startCrop(CAMERA_OPTION)
        }
    }

    private fun getTranslateByPhoto() {
        Toast.makeText(requireContext(), "photo select", Toast.LENGTH_SHORT).show()
        startCrop(PICK_PHOTO_OPTION)
    }

    private fun groupButtonCollapse() {
        viewBinding.apply {
//            cameraBt.startAnimation(cameraButtonDisappearAnim)
            photoBt.startAnimation(photoButtonDisappearAnim)
            cameraBt.visibility = View.GONE
            photoBt.visibility = View.GONE
            if (methodSelect) {
                mainBt.setImageDrawable(cameraIcon)
            } else {
                mainBt.setImageDrawable(photoIcon)
            }
        }
    }

    private fun groupButtonExpand() {
        viewBinding.apply {
            cameraBt.startAnimation(cameraButtonAppearAnim)
            photoBt.startAnimation(photoButtonAppearAnim)
            cameraBt.visibility = View.VISIBLE
            photoBt.visibility = View.VISIBLE
            mainBt.setImageDrawable(cancelIcon)
        }
    }

    private fun animationSetup() {
        photoButtonAppearAnim.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                viewBinding.apply {
                    selectImageContainer.isSelected = true
                    mainBt.isSelected = true
                }
            }

            override fun onAnimationRepeat(p0: Animation?) {

            }

        })

        photoButtonDisappearAnim.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                viewBinding.apply {
                    selectImageContainer.isSelected = false
                    mainBt.isSelected = false
                }
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }
        })
    }

    private fun meaningTvSetup() {
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
        viewBinding.wordEt.movementMethod = ScrollingMovementMethod()
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
                    val oldDes  = destinationLanguage.text
                    val oldSource = sourceLanguage.text
                    destinationLanguage.text = oldSource
                    sourceLanguage.text = oldDes
                    viewLifecycleOwner.lifecycleScope.launch {
                        oldDes?.let {
                            LanguageDSRepository.saveLang(
                                requireContext(),
                                oldDes.toString(),
                                LanguageDSRepository.SOURCE_RECENT
                            )
                        }
                        oldSource?.let {
                            LanguageDSRepository.saveLang(
                                requireContext(),
                                oldSource.toString(),
                                LanguageDSRepository.DES_RECENT
                            )
                        }
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

    companion object {
        private const val CAMERA_OPTION = "camera_option"
        private const val PICK_PHOTO_OPTION = "pick_photo_option"
    }
}