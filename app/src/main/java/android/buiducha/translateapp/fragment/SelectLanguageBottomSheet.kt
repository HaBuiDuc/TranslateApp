package android.buiducha.translateapp.fragment

import android.annotation.SuppressLint
import android.buiducha.translateapp.adapter.LanguageAdapter
import android.buiducha.translateapp.databinding.SelectLanguageFragmentBinding
import android.buiducha.translateapp.util.Language.langList
import android.buiducha.translateapp.viewmodel.SelectLanguageViewModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class SelectLanguageBottomSheet(
    private val sourceLang: String,
    private val destinationLang: String,
    private val onLanguageChange: (String, Int) -> Unit
) : BottomSheetDialogFragment() {
    var target: Int = 0
    private lateinit var viewBinding: SelectLanguageFragmentBinding
    private val viewModel: SelectLanguageViewModel by viewModels()

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

            sourceLanguage.apply {
                setOnTouchListener {_, _ ->
                    onLanguageButtonClick(this)
                    true
                }
            }

            destinationLanguage.apply {
                setOnTouchListener {_, _ ->
                    onLanguageButtonClick(this)
                    true
                }
            }

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
            it.lowercase(Locale.ROOT).contains(filterValue.lowercase(Locale.ROOT))
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

    init {
        Log.d(TAG, sourceLang.toString())
    }

    companion object {
        const val TAG = "SelectLanguageBottomSheet"
    }
}