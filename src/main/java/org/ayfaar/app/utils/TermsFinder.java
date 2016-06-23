package org.ayfaar.app.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;

@Slf4j
@Service
public class TermsFinder {

    @Inject
    TermService termService;

    public Map<String,Integer> getTermsWithFrequency(String content){

        if (content == null || content.isEmpty()) log.info("Content is Empty!");

        Map<String,Integer> termFrequency = new HashMap<>();
        content = content.replace("–","-").replace("—","-");
        StringBuilder result = new StringBuilder(content);

        for (Map.Entry<String, TermService.TermProvider> entry : termService.getAll()) {
            // получаем слово связаное с термином, напрмер "времени" будет связано с термином "Время"
            String word = entry.getKey();
            // составляем условие по которому проверяем есть ли это слов в тексте
            Pattern pattern = compile("(([^A-Za-zА-Яа-я0-9Ёё\\[\\|])|^)(около|слабо|высоко|не|анти|разно|дву|трёх|четырёх|пяти|шести|семи|восьми|девяти|десяти|внутри|пост|меж|мощно|взаимо|внутри|не)?("
                    + word + ")(([^A-Za-zА-Яа-я0-9Ёё\\]\\|])|$)", UNICODE_CHARACTER_CLASS | UNICODE_CASE | CASE_INSENSITIVE);
            Matcher contentMatcher = pattern.matcher(content);
            // если есть:
            int frequency = 0;
            String mainTerm = null;

            if (contentMatcher.find()) {

                Matcher matcher = pattern.matcher(result);
                int offset = 0;

                while (offset < result.length() && matcher.find(offset)) {

                    final TermService.TermProvider termProvider = entry.getValue();
                    boolean hasMainTerm = termProvider.hasMainTerm();
                    final TermService.TermProvider mainTermProvider = hasMainTerm ? termProvider.getMainTerm().get() : null;
                    mainTerm = hasMainTerm ? mainTermProvider.getName() : termProvider.getName();

                    content = contentMatcher.replaceAll(" ");// убираем обработанный термин, чтобы не заменить его более мелким

                    offset = matcher.end();
                    frequency++;
                }

                termFrequency.put(mainTerm, termFrequency.containsKey(mainTerm) ? termFrequency.get(mainTerm) + frequency : frequency);
            }
        }
        return termFrequency;
    }
}
