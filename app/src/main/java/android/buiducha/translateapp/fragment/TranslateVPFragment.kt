package android.buiducha.translateapp.fragment

import android.buiducha.translateapp.databinding.MeaningItemBinding
import android.buiducha.translateapp.model.Vocabulary
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment


class TranslateVPFragment(
    private val vocabulary: Vocabulary
) : Fragment() {

    private lateinit var viewBinding: MeaningItemBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewBinding = MeaningItemBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.apply {
            wordTv.text = vocabulary.sourceText
            meaningTv.text = vocabulary.text.joinToString()
        }
    }


}