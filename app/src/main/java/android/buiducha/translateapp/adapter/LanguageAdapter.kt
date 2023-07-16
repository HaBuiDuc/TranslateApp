package android.buiducha.translateapp.adapter

import android.buiducha.translateapp.databinding.LanguageItemBinding
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class LanguageAdapter(
    private var langList: List<String>,
    private val onItemSelected: (String)  -> Unit
) : Adapter<LanguageHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageHolder {
        Log.d("This is a log", langList.size.toString())
        val viewBinding = LanguageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LanguageHolder(viewBinding)
    }

    override fun getItemCount(): Int = langList.size


    override fun onBindViewHolder(holder: LanguageHolder, position: Int) {
        langList[position].let {langName ->
            holder.bind(langName, onItemSelected)
        }
    }

    fun setLangList(langList: List<String>) {
        this.langList = langList
    }
}
class LanguageHolder(private val viewBinding: LanguageItemBinding) : ViewHolder(viewBinding.root) {
    fun bind(langName: String, onItemSelected: (String) -> Unit) {
        viewBinding.languageTextView.text = langName
        viewBinding.root.setOnClickListener {
            onItemSelected(langName)
        }
    }
}