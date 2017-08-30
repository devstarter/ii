package org.ayfaar.app.utils

import org.ayfaar.app.model.Item
import org.ayfaar.app.services.ItemService
import org.springframework.stereotype.Component
import java.lang.String.format
import java.util.regex.Pattern.*
import javax.inject.Inject

@Component
class TermsMarker @Inject constructor(private val termService: TermService, private val itemService: ItemService) {

    /**
     * Пометить все термины в тексте тегами <term></term>.
     * Например: текст до <term id="термин">термином</term> текст после
     *
     * За основу взять org.ayfaar.app.synchronization.mediawiki.TermSync#markTerms
     *
     * @param content исходный текст с терминами
     * @return текст с тегами терминов
     */
    @JvmOverloads
    fun mark(content: String?, withItems: Boolean? = false): String? {
        if (content == null) return null

        var content = content
        if (content.isEmpty()) return content

        content = content.replace("–", "-").replace("—", "-")

        content = markTerms(content)
        if (withItems == true) content = markItems(content)

        return content
    }

    private fun markTerms(content: String?): String {
        var content = content
        // копируем исходный текст, в этой копии мы будем производить тегирование слов
        val result = StringBuilder(content!!)
        //перед обходом отсортируем по длине термина, сначала самые длинные
        for ((word, termProvider) in termService.all) {
            // получаем слово связаное с термином, напрмер "времени" будет связано с термином "Время"
            // составляем условие по которому проверяем есть ли это слов в тексте
            //Pattern pattern = compile("(([^A-Za-zА-Яа-я0-9Ёё\\[\\|\\-])|^)(" + word
            val pattern = compile("(([^A-Za-zА-Яа-я0-9Ёё\\[|])|^)(ино|до|около|слабо|высоко|не|анти|разно|дву|трёх|четырёх|пяти|шести|семи|восьми|девяти|десяти|внутри|пост|меж|мощно|взаимо|внутри|не)?("
                    + word + ")(([^A-Za-zА-Яа-я0-9Ёё\\]\\|])|$)", UNICODE_CHARACTER_CLASS or UNICODE_CASE or CASE_INSENSITIVE)
            val contentMatcher = pattern.matcher(content!!)
            // если есть:
            if (contentMatcher.find()) {
                // ищем в результирующем тексте
                val matcher = pattern.matcher(result)
                var offset = 0
                // if (matcher.find()) {
                //перенесем обрамления для каждого слова - одно слово может встречаться несколько раз с разными обрамл.
                while (offset < result.length && matcher.find(offset)) {
                    offset = matcher.end()
                    //убедимся что это не уже обработанный термин, а следующий(в им.падеже)
                    //String sub = result.substring(matcher.start() - 3, matcher.start());
                    //if (sub.equals("id=")) {
                    if (wordInTag(result.substring(0, matcher.start()))) {
                        continue
                    }
                    // сохраняем найденое слово из текста так как оно может быть в разных регистрах,
                    // например с большой буквы, или полностью большими буквами
                    val wordPrefix = matcher.group(3)
                    val foundWord = matcher.group(4)
                    val charBefore = if (matcher.group(2) != null) matcher.group(2) else ""
                    val charAfter = if (matcher.group(5) != null) matcher.group(5) else ""
                    // формируем маску для тегирования, title="%s" это дополнительное требования, не описывал ещё в задаче
                    //String replacer = format("%s<term id=\"%s\" title=\"%s\">%s</term>%s",
                    //пока забыли о  title="...."
                    val hasMainTerm = termProvider.hasMain()
                    val mainTermProvider = if (hasMainTerm) termProvider.main.get() else null
                    val hasShortDescription = if (hasMainTerm) mainTermProvider!!.hasShortDescription() else termProvider.hasShortDescription()

                    var attributes = if (hasShortDescription) " has-short-description=\"true\"" else ""
                    attributes += if (hasMainTerm) format(" title=\"%s\"", mainTermProvider!!.name) else ""

                    val replacer = format("%s%s<term id=\"%s\"%s>%s</term>%s",
                            charBefore,
                            wordPrefix ?: "",
                            if (hasMainTerm) mainTermProvider!!.name else termProvider.name,
                            attributes,
                            foundWord,
                            charAfter
                    )
                    //System.out.println("charbefore " + charBefore + " entry " + entry.getValue().getTerm().getName());
                    // заменяем найденое слово тегированным вариантом
                    //result = matcher.replaceAll(replacer);
                    result.replace(matcher.start(), matcher.end(), replacer)
                    //увеличим смещение с учетом замены
                    offset = matcher.start() + replacer.length
                    // убираем обработанный термин, чтобы не заменить его более мелким
                    content = contentMatcher.replaceAll(" ")
                }
            }
        }
        return result.toString()
    }

    private fun markItems(content: String): String {
        var content = content
        val result = StringBuilder(content)
        for (item in itemService.allUriNumbers.values) {

            val pattern = compile("(([^A-Za-zА-Яа-я0-9Ёё]|\\[)|^)($item)(([^A-Za-zА-Яа-я0-9Ёё]|])|$)",
                    UNICODE_CHARACTER_CLASS or UNICODE_CASE or CASE_INSENSITIVE)
            val contentMatcher = pattern.matcher(content)
            if (contentMatcher.find()) {
                val matcher = pattern.matcher(result)
                var offset = 0
                while (offset < result.length && matcher.find(offset)) {
                    offset = matcher.end()
                    if (wordInTag(result.substring(0, matcher.start()))) {
                        continue
                    }
                    val charBefore = if (matcher.group(2) != null) matcher.group(2) else ""
                    val charAfter = if (matcher.group(4) != null) matcher.group(4) else ""

                    val replacer = "$charBefore<uri>${Item.NS}$item</uri>$charAfter"

                    result.replace(matcher.start(), matcher.end(), replacer)
                    offset = matcher.start() + replacer.length
                    content = contentMatcher.replaceAll(" ")
                }
            }
        }
        return result.toString()
    }

    private fun wordInTag(substring: String): Boolean {
        val startTag = substring.lastIndexOf("<term id=")
        val endTag = substring.lastIndexOf("</term>")

        return startTag >= 0 && startTag > endTag
    }
}
