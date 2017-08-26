package com.marverenic.reader.model

private val HTML_TAG_REGEX = Regex("""<[^>]*>""")

data class Content(val content: String) {

    @Transient private var plaintextContent: String? = null
    @Transient private var summary: String? = null

    fun asPlaintext(): String {
        if (plaintextContent == null) {
            plaintextContent = content.replace(HTML_TAG_REGEX, "").trim()
        }

        return plaintextContent!!
    }

    fun summary(): String {
        if (summary == null) {
            summary = asPlaintext().substringBefore("\n")
        }

        return summary!!
    }

}