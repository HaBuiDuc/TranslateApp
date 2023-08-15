package android.buiducha.translateapp.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

fun copyToClipboard(context: Context, data: String) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val label = "translation"
    val clipData = ClipData.newPlainText(label, data)
    clipboardManager.setPrimaryClip(clipData)
    Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
}