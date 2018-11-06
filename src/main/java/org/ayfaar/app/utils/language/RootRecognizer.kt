package org.ayfaar.app.utils.language

class RootRecognizer {
    private val endings = listOf("ентный", "ность", "ный", "ский", "но", "ция", "льность", "ивный", "ы", "ировать", "ческий", "нация", "ионность", "ованный", "вание")
            .sortedByDescending { it.length }

    fun recognize(text: String): String {
        if (text == text.toUpperCase()) return text // code or abbreviation

        endings.forEach { ending ->
            if (text.endsWith(ending)) return text.substring(0, text.length - ending.length)
        }
        return /*if (text.length > 7) text.substring(0, text.length - 4) else */text
    }
}