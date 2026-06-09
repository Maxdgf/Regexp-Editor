package com.maxdgf.regexer.core.system_utils

import android.content.Context
import android.net.Uri
import java.io.IOException

class FileManager(context: Context) {
    private val contentResolver = context.contentResolver // get content resolver

    /**
     * Opens text file by uri and returns text file content.
     * @param uri document file uri.
     * @return file content.
     */
    fun openTextFile(uri: Uri): String {
        val fileContent = StringBuilder()

        try {
            val inputStream = contentResolver.openInputStream(uri)

            fileContent.append(
                inputStream?.use { stream ->
                    stream.readBytes().decodeToString() // decode bytes to string
                } ?: ""
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return fileContent.toString()
    }
}