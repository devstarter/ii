package org.ayfaar.app.services;

import one.util.streamex.StreamEx;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Translation;
import org.ayfaar.app.utils.Language;
import org.ayfaar.app.utils.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.regex.Pattern.*;

//@Service
public class TranslationService {
    private final String MARK_IN = "‹";
    private final String MARK_OUT = "›";
    private final List<String> endings;
    private CommonDao commonDao;

    private Map<Language, List<Pair<String, String>>> map = new LinkedHashMap<>();

    @Inject
    public TranslationService(CommonDao commonDao, ResourceLoader resourceLoader) throws IOException {
        this.commonDao = commonDao;
        final Resource endingsResource = resourceLoader.getResource(ResourceLoader.CLASSPATH_URL_PREFIX + "translation/endings.txt");
        endings = FileUtils.readLines(endingsResource.getFile());
        endings.sort((o1, o2) -> Integer.compare(o2.length(), o1.length()));
        load();
    }

    private void load() {
        StreamEx.of(commonDao.getAll(Translation.class))
                .groupingBy(Translation::getLang)
                .forEach((language, translations) -> {
                    final List<Pair<String, String>> list = StreamEx.of(translations)
                            .map(t -> Pair.of(t.getOrigin(), t.getTranslated()))
                            .sorted((o1, o2) -> Integer.compare(o2.getKey().length(), o1.getKey().length()))
                            .toList();
                    this.map.put(language, list);
                });
    }

    public String translate(String content, Language language) {
        if (content == null || content.isEmpty()) return content;

        StringBuilder result = new StringBuilder(content);

        String _endings = org.apache.commons.lang3.StringUtils.join(endings, "|");

        for (Pair<String, String> entry : map.get(language)) {
            // получаем слово связаное с термином, напрмер "времени" будет связано с термином "Время"
            String word = entry.getKey();
            // составляем условие по которому проверяем есть ли это слов в тексте
            //Pattern pattern = compile("(([^A-Za-zА-Яа-я0-9Ёё\\[\\|\\-])|^)(" + word
            Pattern pattern = compile("(([^A-Za-zА-Яа-я0-9Ёё\\[\\|])|^)(" + word +")(" + _endings + ")?", UNICODE_CHARACTER_CLASS | UNICODE_CASE | CASE_INSENSITIVE);
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
                    // сохраняем найденое слово из текста так как оно может быть в разных регистрах,
                    // например с большой буквы, или полностью большими буквами
                    if (wordInTranslated(result.substring(0, matcher.start()))) {
                        continue;
                    }

                    String charBefore = matcher.group(2) != null ? matcher.group(2) : "";
                    if (charBefore.equals(MARK_IN)) continue;

                    String foundWord = matcher.group(3);
                    String ending = matcher.group(4) != null ? matcher.group(4) : "";
                    final String translated = changeCase(foundWord, entry.getValue());
                    String replacement = format("%s"+MARK_IN+"%s%s"+MARK_OUT+"%s",
                            charBefore,
                            foundWord,
                            ending,
                            translated
                    );
                    result.replace(matcher.start(), matcher.end(), replacement);
                    //увеличим смещение с учетом замены
                    offset = matcher.start() + replacement.length();
                    // убираем обработанный термин, чтобы не заменить его более мелким
                    content = contentMatcher.replaceAll(" ");
                }
            }
        }
        return result.toString();
    }

    private String changeCase(String foundWord, String translation) {
        if (match("^[А-ЯЁа-яё]+$", foundWord)) {
            if (match("^[А-ЯЁ][а-яё]+$", foundWord)) return StringUtils.firstUpper(translation);
            if (match("^[А-ЯЁ]+$", foundWord)) return translation.toUpperCase();
            if (match("^[а-яё]+$", foundWord)) return translation.toLowerCase();
        }
        return translation;
    }

    private boolean match(String regexp, String word) {
        return Pattern.compile(regexp, UNICODE_CHARACTER_CLASS | UNICODE_CASE).matcher(word).matches();
    }

    private boolean wordInTranslated(String substring) {
        int startTag = substring.lastIndexOf(MARK_IN);
        int endTag = substring.lastIndexOf(MARK_OUT);

        return startTag >= 0 && startTag > endTag;
    }
}
