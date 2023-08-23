package android.buiducha.translateapp.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.buiducha.translateapp.R
import android.buiducha.translateapp.adapter.LanguageAdapter
import android.buiducha.translateapp.databinding.SelectLanguageFragmentBinding
import android.buiducha.translateapp.util.Language.langList
import android.buiducha.translateapp.viewmodel.SelectLangViewModel
import android.content.Context
import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import java.util.*


class SelectLanguageBottomSheet(
    private val sourceLang: String,
    private val destinationLang: String,
    private val onLanguageChange: (String, Int) -> Unit
) : BottomSheetDialogFragment() {
    var target: Int = 0
    private lateinit var viewBinding: SelectLanguageFragmentBinding
    private val viewModel: SelectLangViewModel by viewModels()
    private val animCollapse: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.language_recent_collapse)
    }
    private val animAppear: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.language_recent_appear)
    }
    private var isCollapse = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = SelectLanguageFragmentBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHideKeyboard(view)
        setLanguageButtonState()

        viewBinding.apply {
            val languageAdapter = LanguageAdapter(viewModel.language.langList) { lang ->
                if (target == 0) {
                    sourceLanguage.text = lang
                } else {
                    destinationLanguage.text = lang
                }
                viewModel.saveRecentLang(requireContext(), lang, target)
                onLanguageChange(lang, target)
                hideKeyboard(root)
            }
            languageRecyclerView.adapter = languageAdapter
            languageRecyclerView.layoutManager = LinearLayoutManager(context)

            viewModel.getRecentLang(requireContext(), target)
            viewModel.recentLang.observe(viewLifecycleOwner) {
                recentLangRecyclerView.adapter = LanguageAdapter(it!!) {lang ->
                    if (target == 0) {
                        sourceLanguage.text = lang
                    } else {
                        destinationLanguage.text = lang
                    }
                    onLanguageChange(lang, target)
                    hideKeyboard(root)
                }
                Log.d(TAG, it.toString())
            }
            recentLangRecyclerView.layoutManager = LinearLayoutManager(context)

            searchViewSetup(view, languageAdapter)

        }

        languageSelectSetup()

        recentLangCollapse()
    }

    private fun searchViewSetup(
        view: View,
        languageAdapter: LanguageAdapter
    ) {
        viewBinding.apply {
            languageSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    hideKeyboard(view)
                    return true
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.run {
                        languageAdapter.setLangList(filterList(this))
                        languageAdapter.notifyDataSetChanged()
                    }
                    return true
                }
            })
        }
    }

    private fun recentLangCollapse() {
        viewBinding.recentLangTextView.setOnClickListener {
            viewBinding.recentLangRecyclerView.apply {
                if (isCollapse) {
                    visibility = View.VISIBLE
                    startAnimation(animAppear)
                    isCollapse = false
                    isSelected = false
                } else {
                    startAnimation(animCollapse)
                    visibility = View.GONE
                    isCollapse = true
                    isSelected = true
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun languageSelectSetup() {
        viewBinding.apply {
            sourceLanguage.apply {
                setOnTouchListener { _, _ ->
                    onLanguageButtonClick(this)
                    true
                }
            }

            destinationLanguage.apply {
                setOnTouchListener { _, _ ->
                    onLanguageButtonClick(this)
                    true
                }
            }
        }
    }

    private fun onLanguageButtonClick(view: View) {
        if (!view.isPressed) {
            view.isPressed = true
            if (target == 0) {
                viewBinding.sourceLanguage.isPressed = false
                target = 1
            } else {
                viewBinding.destinationLanguage.isPressed = false
                target = 0
            }
            viewModel.getRecentLang(requireContext(), target)
        }
        hideKeyboard(view)
    }

    private fun setLanguageButtonState() {
        viewBinding.apply {
            if (target == 0) {
                sourceLanguage.isPressed = true
            } else {
                destinationLanguage.isPressed = true
            }
            sourceLanguage.text = sourceLang
            destinationLanguage.text = destinationLang
        }
    }

    private fun setUpRecyclerView() {
        viewBinding.apply {
            val languageAdapter = LanguageAdapter(langList) { lang ->
                if (target == 0) {
                    sourceLanguage.text = lang
                } else {
                    destinationLanguage.text = lang
                }
                onLanguageChange(lang, target)
                viewLifecycleOwner.lifecycleScope.launch {
                }
                hideKeyboard(root)
            }

            languageRecyclerView.adapter = languageAdapter
            languageRecyclerView.layoutManager = LinearLayoutManager(context)

            recentLangRecyclerView.layoutManager = LinearLayoutManager(context)
        }
    }

    private fun filterList(filterValue: String): List<String> {
       return langList.filter {
            it.lowercase(Locale.ROOT).contains(filterValue.trim().lowercase(Locale.ROOT))
       }
    }

    private fun hideKeyboard(view: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setHideKeyboard(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is SearchView) {
            view.setOnTouchListener { _, _ ->
                hideKeyboard(view)
                false
            }
            //If a layout container, iterate over children and seed recursion.
            if (view is ViewGroup) {
                for (i in 0 until view.childCount) {
                    val innerView = view.getChildAt(i)
                    setHideKeyboard(innerView)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val bottomSheet =
            (dialog as BottomSheetDialog?)!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        if (bottomSheet != null) {
            // set the bottom sheet state to Expanded to expand to the entire window
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            // disable the STATE_HALF_EXPANDED state
            BottomSheetBehavior.from(bottomSheet).skipCollapsed = true

            val screenHeight = getScreenHeight(requireActivity()) * (2 / 3).toFloat()
            BottomSheetBehavior.from(bottomSheet).peekHeight = 800
            BottomSheetBehavior.from(bottomSheet).isDraggable = false
            // set the bottom sheet height to match_parent
//            val layoutParams = bottomSheet.layoutParams
//            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
//            Log.d(TAG, WindowManager.LayoutParams.MATCH_PARENT.toString())
//            bottomSheet.layoutParams = layoutParams

        }

        // Make the dialog cover the status bar
        dialog!!.window!!.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
    private fun getScreenHeight(activity: Activity): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
    }
    init {
        Log.d(TAG, sourceLang)
    }

    companion object {
        const val TAG = "SelectLanguageBottomSheet"
    }
}