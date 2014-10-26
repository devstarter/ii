package org.ayfaar.app.utils;

import org.ayfaar.app.model.Term;
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

    @Inject TermsMap termsMap;

    /**
     * Пометить все термины в тексте тегами <term></term>.
     * Например: текст до <term id="термин">термином</term> текст после
     *
     * За основу взять org.ayfaar.app.synchronization.mediawiki.TermSync#markTerms
     *
     * @see org.ayfaar.app.synchronization.mediawiki.TermSync#markTerms
     * @param content исходный текст с терминами
     * @return текст с тегами терминов
     */
    public String mark(String content) {
        // копируем исходный текст, в этой копии мы будем производить тегирование слов
        String result = content;

        for (Map.Entry<String, Term> entry : termsMap.getAll()) {
            // получаем слово связаное с термином, напрмер "времени" будет связано с термином "Время"
            String word = entry.getKey();
            // составляем условие по которому проверяем есть ли это слов в тексте
            Pattern pattern = compile("(([^A-Za-zА-Яа-я0-9Ёё\\[\\|\\-])|^)(" + word
                    + ")(([^A-Za-zА-Яа-я0-9Ёё\\]\\|])|$)", UNICODE_CHARACTER_CLASS | UNICODE_CASE | CASE_INSENSITIVE);
            Matcher contentMatcher = pattern.matcher(content);
            // если есть:
            if (contentMatcher.find()) {
                // ищем в результирующем тексте
                Matcher matcher = pattern.matcher(result);
                if (matcher.find()) {
                    // сохраняем найденое слово из текста так как оно может быть в разных регистрах,
                    // например с большой буквы, или полностью большими буквами
                    String foundWord = contentMatcher.group(3);
                    String charBefore = contentMatcher.group(2) != null ? contentMatcher.group(2) : "";
                    String charAfter = contentMatcher.group(5) != null ? contentMatcher.group(5) : "";
                    // формируем маску для тегирования, title="%s" это дополнительное требования, не описывал ещё в задаче
                    //String replacer = format("%s<term id=\"%s\" title=\"%s\">%s</term>%s",
                    //пока забыли о  title="...."
                    final String description = entry.getValue().getShortDescription();
                    String replacer = format("%s<term id=\"%s\"%s>%s</term>%s",
                            charBefore,
                            entry.getValue().getName(),
                            description != null && !description.isEmpty() ? " hasDescription=\"true\"" : "",
                            foundWord,
                            charAfter
                    );
                    // заменяем найденое слово тегированным вариантом
                    result = matcher.replaceAll(replacer);
                }
            }
        }
        return result;
       // throw new NotImplementedException();
    }
}
