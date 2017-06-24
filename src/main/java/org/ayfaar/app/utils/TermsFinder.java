package org.ayfaar.app.utils;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.TermParagraph;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;

@Slf4j
@Service
public class TermsFinder {

    @Inject TermService termService;
    @Inject ContentsService contentsService;
    @Inject
    CommonDao commonDao;

    public Map<String,Integer> getTermsWithFrequency(String content){
        return getTermsWithFrequency(content, null);
    }

    public Map<String,Integer> getTermsWithFrequency(String content, String term){

        if (content == null || content.isEmpty()) log.info("Content is Empty!");

        Map<String,Integer> termFrequency = new HashMap<>();
        content = content.replace("–","-").replace("—","-");
        StringBuilder result = new StringBuilder(content);

        List<Map.Entry<String, TermService.TermProvider>> allTerms = (term != null) ? getSimpleTermProvider(term) : termService.getAll();


        for (Map.Entry<String, TermService.TermProvider> entry : allTerms) {

            int frequency = 0;
            String mainTerm = null;
            // получаем слово связаное с термином, напрмер "времени" будет связано с термином "Время"
            String word = entry.getKey();
            // составляем условие по которому проверяем есть ли это слов в тексте
            Pattern pattern = compile("(([^A-Za-zА-Яа-я0-9Ёё\\[\\|])|^)(около|слабо|высоко|не|анти|разно|дву|трёх|четырёх|пяти|шести|семи|восьми|девяти|десяти|внутри|пост|меж|мощно|взаимо|внутри|не)?("
                    + word + ")(([^A-Za-zА-Яа-я0-9Ёё\\]\\|])|$)", UNICODE_CHARACTER_CLASS | UNICODE_CASE | CASE_INSENSITIVE);
            Matcher contentMatcher = pattern.matcher(content);
            // если есть:

            if (contentMatcher.find()) {
                int offset = 0;
                Matcher matcher = pattern.matcher(result);

                while (offset < result.length() && matcher.find(offset)) {

                    final TermService.TermProvider termProvider = entry.getValue();
                    mainTerm = termProvider.getMainOrThis().getName();

                    content = contentMatcher.replaceAll(" ");// убираем обработанный термин, чтобы не заменить его более мелким

                    offset = matcher.end();
                    frequency++;
                }

                if(mainTerm != null)
                    termFrequency.put(mainTerm, termFrequency.containsKey(mainTerm) ? termFrequency.get(mainTerm) + frequency : frequency);
            }
        }
        return termFrequency;
    }

    public void updateTermParagraphForTerm(String new_term){

        contentsService.getAllParagraphs().forEach(paragraph ->
        {
            Map<String, Integer> termsWithFrequency = getTermsWithFrequency(paragraph.description(), new_term);
            termsWithFrequency.keySet().parallelStream().map(term ->
                    new TermParagraph(paragraph.code(), term)).forEach(t ->
                    commonDao.save(TermParagraph.class,t));
        });
    }

    private List<Map.Entry<String,TermService.TermProvider>> getSimpleTermProvider(String term) {
        Map<String,TermService.TermProvider> termProviderMap = new HashMap<>();
        TermService.TermProvider termProvider = termService.get(term).get();
        List<String> morphs = termProvider.getMorphs();
        for (String morph :
                morphs) {

            if(!termProviderMap.containsKey(morph)) {
                termProviderMap.put(morph, termProvider);
            }

        }

        return new ArrayList<>(termProviderMap.entrySet());
    }
}