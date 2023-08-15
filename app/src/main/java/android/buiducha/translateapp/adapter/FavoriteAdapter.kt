package android.buiducha.translateapp.adapter

import android.buiducha.translateapp.databinding.FavoriteItemBinding
import android.buiducha.translateapp.model.Vocabulary
import android.buiducha.translateapp.util.langCap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class FavoriteAdapter(
    private var vocabularyList: List<Vocabulary>,
    private val favVocabRemove: (Vocabulary) -> Unit
) : Adapter<FavoriteHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteHolder {
        val binding = FavoriteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteHolder(binding, favVocabRemove)
    }

    override fun getItemCount(): Int = vocabularyList.size


    override fun onBindViewHolder(holder: FavoriteHolder, position: Int) {
        vocabularyList[position].let {
            holder.bind(it)
        }
    }

}
class FavoriteHolder(
    private val binding: FavoriteItemBinding,
    private val favVocabRemove: (Vocabulary) -> Unit
) : ViewHolder(binding.root) {
    fun bind(vocabulary: Vocabulary) {
        binding.apply {
            wordTv.text = vocabulary.sourceText
            meaningTv.text = vocabulary.text.joinToString()
            langPairTv.text = vocabulary.langCap()
            wordFavorite.isSelected = true
            wordFavorite.setOnClickListener {
                favVocabRemove(vocabulary)
            }
        }
    }
}