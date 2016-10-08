package org.ayfaar.app.utils;

import org.ayfaar.app.utils.TermService.TermProvider;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.regex.Pattern.*;

@Component
public class TermsMarker {
    public final static String TAG_NAME = "term";

    @Inject TermService termService;

    /**
     * Пометить все термины в тексте тегами <term></term>.
     * Например: текст до <term id="термин">термином</term> текст после
     *
     * За основу взять org.ayfaar.app.synchronization.mediawiki.TermSync#markTerms
     *
     * @param content исходный текст с терминами
     * @return текст с тегами терминов
     */
    public String mark(String content) {
        if (content == null || content.isEmpty()) return content;

        content = content.replace("–","-").replace("—","-");
        // копируем исходный текст, в этой копии мы будем производить тегирование слов
        StringBuilder result = new StringBuilder(content);
        //перед обходом отсортируем по длине термина, сначала самые длинные


        for (Map.Entry<String, TermProvider> entry : termService.getAll()) {
            // получаем слово связаное с термином, напрмер "времени" будет связано с термином "Время"
            String word = entry.getKey();
            // составляем условие по которому проверяем есть ли это слов в тексте
            //Pattern pattern = compile("(([^A-Za-zА-Яа-я0-9Ёё\\[\\|\\-])|^)(" + word
            Pattern pattern = compile("(([^A-Za-zА-Яа-я0-9Ёё\\[\\|])|^)(около|слабо|высоко|не|анти|разно|дву|трёх|четырёх|пяти|шести|семи|восьми|девяти|десяти|внутри|пост|меж|мощно|взаимо|внутри|не)?("
                    + word + ")(([^A-Za-zА-Яа-я0-9Ёё\\]\\|])|$)", UNICODE_CHARACTER_CLASS | UNICODE_CASE | CASE_INSENSITIVE);
            Matcher contentMatcher = pattern.matcher(content);
            // если есть:
            if (contentMatcher.find()) {
                // ищем в результирующем тексте
                Matcher matcher = pattern.matcher(result);
                int offset = 0;
                // if (matcher.find()) {
                //перенесем обрамления для каждого слова - одно слово может встречаться несколько раз с разными обрамл.
                while (offset < result.length() && matcher.find(offset)) {
                    offset = matcher.end();
                    //убедимся что это не уже обработанный термин, а следующий(в им.падеже)
                    //String sub = result.substring(matcher.start() - 3, matcher.start());
                    //if (sub.equals("id=")) {
                    if (wordInTag(result.substring(0, matcher.start()))) {
                        continue;
                    }
                    // сохраняем найденое слово из текста так как оно может быть в разных регистрах,
                    // например с большой буквы, или полностью большими буквами
                    String wordPrefix = matcher.group(3);
                    String foundWord = matcher.group(4);
                    String charBefore = matcher.group(2) != null ? matcher.group(2) : "";
                    String charAfter = matcher.group(5) != null ? matcher.group(5) : "";
                    // формируем маску для тегирования, title="%s" это дополнительное требования, не описывал ещё в задаче
                    //String replacer = format("%s<term id=\"%s\" title=\"%s\">%s</term>%s",
                    //пока забыли о  title="...."
                    final TermProvider termProvider = entry.getValue();
                    boolean hasMainTerm = termProvider.hasMain();
                    final TermProvider mainTermProvider = hasMainTerm ? termProvider.getMain().get() : null;
                    boolean hasShortDescription = hasMainTerm ? mainTermProvider.hasShortDescription() : termProvider.hasShortDescription();

                    String attributes = hasShortDescription ? " has-short-description=\"true\"" : "";
                    attributes += hasMainTerm ?  format(" title=\"%s\"", mainTermProvider.getName()) : "";

                    String replacer = format("%s%s<term id=\"%s\"%s>%s</term>%s",
                            charBefore,
                            wordPrefix == null ? "" : wordPrefix,
                            hasMainTerm ? mainTermProvider.getName() : termProvider.getName(),
                            attributes,
                            foundWord,
                            charAfter
                    );
                    //System.out.println("charbefore " + charBefore + " entry " + entry.getValue().getTerm().getName());
                    // заменяем найденое слово тегированным вариантом
                    //result = matcher.replaceAll(replacer);
                    result.replace(matcher.start(), matcher.end(), replacer);
                    //увеличим смещение с учетом замены
                    offset = matcher.start() + replacer.length();
                    // убираем обработанный термин, чтобы не заменить его более мелким
                    content = contentMatcher.replaceAll(" ");
                }
            }
        }
        return result.toString();
    }

    private boolean wordInTag(String substring) {
        int startTag = substring.lastIndexOf("<term id=");
        int endTag = substring.lastIndexOf("</term>");

        return startTag >= 0 && startTag > endTag;
    }
}
