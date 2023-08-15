package android.buiducha.translateapp.viewmodel

import android.buiducha.translateapp.model.Vocabulary
import android.buiducha.translateapp.repository.FavoriteRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FavVocabViewModel : ViewModel() {
    private val repository = FavoriteRepository.get()
    val favList = repository.getVocabularies()

    fun deleteVocab(vocabulary: Vocabulary) {
        viewModelScope.launch {
            repository.deleteVocabulary(vocabulary)
        }
    }
}