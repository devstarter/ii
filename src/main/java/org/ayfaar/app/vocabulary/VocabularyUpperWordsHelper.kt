package org.ayfaar.app.vocabulary

import org.ayfaar.app.services.GoogleSpreadsheetService
import org.ayfaar.app.utils.TermUtils
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class VocabularyUpperWordsHelper(/*private val termService: TermService*/) {
    private var upperWords: Collection<String>? = null

    fun check(text: String): String {
        if (upperWords == null) {
            upperWords = loadUpperWords()//upperTermsJson.fromJson()//termService.allNames.filter { it == it.toUpperCase() }
        }
        return makeCodesUpper(text, upperWords!!)
    }

    internal fun loadUpperWords(): Collection<String> {
        val service = GoogleSpreadsheetService()
        val spreadsheetId = "1Tv6rXLp8A3XkGXDUtotX9iyPB1yhp0i7cLQ9ocscgEo"
        return service.read(spreadsheetId, "Словарь ЗКК!A:Z")
                .map { it[0] as String }
                .filterIndexed { index, _ -> index != 0 } // skip  head
                .map { item -> TermUtils.getNonCosmicCodePart(item)?.let { item.replace(it, "") } ?: item }
    }
}

internal fun makeCodesUpper(text: String, upperWords: Collection<String>): String {
    var content = text
    // копируем исходный текст, в этой копии мы будем производить тегирование слов
    val result = StringBuilder(content)
    //перед обходом отсортируем по длине термина, сначала самые длинные
    for (upperWord in upperWords) {
        val pattern = Pattern.compile("""(([^A-Za-zА-Яа-я0-9Ёё\[|])|^)($upperWord)(([^A-Za-zА-Яа-я0-9Ёё\]|])|$)""",
                Pattern.UNICODE_CHARACTER_CLASS or Pattern.UNICODE_CASE or Pattern.CASE_INSENSITIVE)
        val contentMatcher = pattern.matcher(content)

        if (contentMatcher.find()) {
            val matcher = pattern.matcher(result)
            var offset = 0
            while (offset < result.length && matcher.find(offset)) {
                val charBefore = if (matcher.group(2) != null) matcher.group(2) else ""
                val charAfter = if (matcher.group(4) != null) matcher.group(4) else ""

                val replacer = "$charBefore$upperWord$charAfter"
                result.replace(matcher.start(), matcher.end(), replacer)
                offset = matcher.start() + replacer.length
                // убираем обработанный термин, чтобы не заменить его более мелким
                content = contentMatcher.replaceAll(" ")
            }
        }
    }
    return result.toString()
}
