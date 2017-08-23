package com.marverenic.reader.model

import android.os.Build
import android.text.Html

private const val OBJECT_REPLACEMENT_CHARACTER = (0xFFFC).toChar()

data class Content(val content: String) {

    @Transient private var plaintextContent: String? = null

    fun asPlaintext(): String {
        if (plaintextContent == null) {
            plaintextContent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(content, 0)
            } else {
                @Suppress("DEPRECATION")
                Html.fromHtml(content)
            }.toString()
                    .filter { it != OBJECT_REPLACEMENT_CHARACTER }
                    .trim()
                    .substringBefore("\n")
        }

        return plaintextContent!!
    }

}