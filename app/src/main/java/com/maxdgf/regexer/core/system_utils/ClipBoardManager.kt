package com.maxdgf.regexer.core.system_utils

import android.content.ClipboardManager
import android.content.Context

class ClipBoardManager(context: Context) {
    private val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    /**
     * Returns text from clipboard.
     * @return text from clipboard.
     */
    fun getClipboardText(): String {
        val clipItemText = StringBuilder() // clip text string builder

        val clipData = clipboard.primaryClip // current primary clip in the clipboard

        clipData?.let { data ->
            val clipItem = data.getItemAt(0) // get first clip item
            clipItemText.append(clipItem.text.toString())
        }

        return clipItemText.toString()
    }
}