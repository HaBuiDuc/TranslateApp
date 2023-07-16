package android.buiducha.translateapp.adapter

import android.buiducha.translateapp.model.Vocabulary
import android.buiducha.translateapp.databinding.MeaningItemBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class TranslateAdapter(
    private var vocabularyList: List<Vocabulary>
) : Adapter<TranslateHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TranslateHolder {
        val binding = MeaningItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TranslateHolder(binding)
    }

    override fun getItemCount(): Int = vocabularyList.size


    override fun onBindViewHolder(holder: TranslateHolder, position: Int) {
        vocabularyList[position].let {
            holder.bind(it)
        }
    }

}
class TranslateHolder(private val binding: MeaningItemBinding) : ViewHolder(binding.root) {
    fun bind(vocabulary: Vocabulary) {
        binding.apply {
        }
    }
}