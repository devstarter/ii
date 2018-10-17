package org.ayfaar.app.utils

import java.util.*

object Transliterator {

    private val charMap = HashMap<Char, String>()

    init {
        charMap['А'] = "A"
        charMap['Б'] = "B"
        charMap['В'] = "V"
        charMap['Г'] = "G"
        charMap['Д'] = "D"
        charMap['Е'] = "E"
        charMap['Ё'] = "E"
        charMap['Ж'] = "Zh"
        charMap['З'] = "Z"
        charMap['И'] = "I"
        charMap['Й'] = "Y"
        charMap['К'] = "K"
        charMap['Л'] = "L"
        charMap['М'] = "M"
        charMap['Н'] = "N"
        charMap['О'] = "O"
        charMap['П'] = "P"
        charMap['Р'] = "R"
        charMap['С'] = "S"
        charMap['Т'] = "T"
        charMap['У'] = "U"
        charMap['Ф'] = "F"
        charMap['Х'] = "H"
        charMap['Ц'] = "C"
        charMap['Ч'] = "Ch"
        charMap['Ш'] = "Sh"
        charMap['Щ'] = "Sh"
        charMap['Ъ'] = "'"
        charMap['Ы'] = "Y"
        charMap['Ь'] = "'"
        charMap['Э'] = "E"
        charMap['Ю'] = "U"
        charMap['Я'] = "Ya"
        charMap['а'] = "a"
        charMap['б'] = "b"
        charMap['в'] = "v"
        charMap['г'] = "g"
        charMap['д'] = "d"
        charMap['е'] = "e"
        charMap['ё'] = "e"
        charMap['ж'] = "zh"
        charMap['з'] = "z"
        charMap['и'] = "i"
        charMap['й'] = "y"
        charMap['к'] = "k"
        charMap['л'] = "l"
        charMap['м'] = "m"
        charMap['н'] = "n"
        charMap['о'] = "o"
        charMap['п'] = "p"
        charMap['р'] = "r"
        charMap['с'] = "s"
        charMap['т'] = "t"
        charMap['у'] = "u"
        charMap['ф'] = "f"
        charMap['х'] = "h"
        charMap['ц'] = "c"
        charMap['ч'] = "ch"
        charMap['ш'] = "sh"
        charMap['щ'] = "sh"
        charMap['ъ'] = ""
        charMap['ы'] = "y"
        charMap['ь'] = ""
        charMap['э'] = "e"
        charMap['ю'] = "u"
        charMap['я'] = "ya"

    }

    fun transliterate(string: String): String {
        val transliteratedString = StringBuilder()
        for (i in 0 until string.length) {
            val ch = string[i]
            val charFromMap = charMap[ch]
            if (charFromMap == null) {
                transliteratedString.append(ch)
            } else {
                transliteratedString.append(charFromMap)
            }
        }
        return transliteratedString.toString()
    }

    fun forUrl(string: String): String {
        val transliteratedString = StringBuilder()
        for (i in 0 until string.length) {
            val ch = string[i]
            val charFromMap = charMap[ch]
            if (charFromMap == null) {
                transliteratedString.append(ch)
            } else {
                transliteratedString.append(charFromMap)
            }
        }
        return transliteratedString.toString().replace(Regex("[\\s+,.]+"), "-").toLowerCase()
    }
}