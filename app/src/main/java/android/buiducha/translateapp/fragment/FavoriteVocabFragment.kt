package android.buiducha.translateapp.fragment

import android.buiducha.translateapp.adapter.FavoriteAdapter
import android.buiducha.translateapp.databinding.FavoriteFragmentBinding
import android.buiducha.translateapp.viewmodel.FavVocabViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch

class FavoriteVocabFragment : Fragment() {
    private lateinit var viewBinding: FavoriteFragmentBinding
    private val viewModel: FavVocabViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewBinding = FavoriteFragmentBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onPause() {
        super.onPause()
        Log.d("This is a log", "onPause: favorite")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvSetup()
    }

    private fun rvSetup() {
        viewBinding.favoriteRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            lifecycleScope.launch {
                viewModel.favList.collect {favVocabs ->
                    adapter = FavoriteAdapter(favVocabs) {
                        viewModel.deleteVocab(it)
                    }
                }
            }
        }
    }
}