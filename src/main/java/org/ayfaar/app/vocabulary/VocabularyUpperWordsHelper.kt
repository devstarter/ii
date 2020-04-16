package org.ayfaar.app.vocabulary

import mu.KotlinLogging
import org.ayfaar.app.utils.TermService
import org.springframework.stereotype.Component
import java.util.regex.Pattern
import javax.inject.Inject

@Component
class VocabularyUpperWordsHelper {
    @Inject lateinit var termService: TermService

    private var upperWords: Collection<String>? = null
    private val logger = KotlinLogging.logger {  }

    fun check(text: String): String {
        if (upperWords == null) {
            upperWords = loadUpperWords(termService.allNames)//upperTermsJson.fromJson()//termService.allNames.filter { it == it.toUpperCase() }
        }
        return makeCodesUpper(text, upperWords!!)
    }

    /*internal fun loadUpperWords(): Collection<String> {
        val service = GoogleSpreadsheetService()
        val spreadsheetId = "1Tv6rXLp8A3XkGXDUtotX9iyPB1yhp0i7cLQ9ocscgEo"
        return try {
            service.read(spreadsheetId, "Словарь ЗКК!A:Z")
                    .map { it[0] as String }
                    .filterIndexed { index, _ -> index != 0 } // skip  head
                    .map { item -> TermUtils.getNonCosmicCodePart(item)?.let { item.replace(it, "") } ?: item }
        } catch (e: Exception) {
            logger.warn("Error while getting upper case words", e)
            emptyList()
        }
    }*/
}

internal fun loadUpperWords(terms: List<String>): List<String> {
    val regex = Regex("([A-ZА-ЯЁ-]+)(-[A-Za-zА-Яа-я0-9Ёё-]+)?")
    return terms.mapNotNull { regex.matchEntire(it)?.groups?.get(1)?.value }
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
