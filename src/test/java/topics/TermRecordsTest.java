package topics;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.TermRecordFrequency;
import org.ayfaar.app.services.record.RecordService;
import org.ayfaar.app.utils.TermService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;


@Slf4j
public class TermRecordsTest extends IntegrationTest {

    @Autowired
    CommonDao commonDao;
    @Autowired
    ResourceLoader resourceLoader;
    @Autowired
    RecordService recordService;
    @Autowired
    TermService termService;

    @Test
    public void parseTest() throws Exception {
        File folder = resourceLoader.getResource("classpath:topics").getFile();
        Map<String, String> recordsWithText = WordParserService.parse(folder);
        recordsWithText.entrySet().stream().forEach(e -> termRecordFrequency(e.getKey(),e.getValue()));
    }

    @Test
    public void getRecordsWithTermTest(){
        recordService.getRecordsWithTerm("Форма").stream().forEach(System.out::println);
    }

    @Test
    public void getByFrequencyTest() {
        List<TermRecordFrequency> byFrequency = recordService.getByFrequency();
        byFrequency.stream().map(TermRecordFrequency::getTerm).forEach(System.out::println);
    }

    private TermRecordFrequency createTermRecordFrequency(String record, String term, int frequency){
        TermRecordFrequency termRecordFrequency = new TermRecordFrequency();
        termRecordFrequency.setRecord(record);
        termRecordFrequency.setTerm(term);
        termRecordFrequency.setFrequency(frequency);
        return termRecordFrequency;
    }

    private void termRecordFrequency(String record, String content) {

        if (content == null || content.isEmpty()) return;
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
            String findWord = null;
            if (contentMatcher.find()) {

                Matcher matcher = pattern.matcher(result);
                int offset = 0;

                while (offset < result.length() && matcher.find(offset)) {
                    frequency++;
                    offset = matcher.end();
                    String foundWord = matcher.group(4);

                    final TermService.TermProvider termProvider = entry.getValue();
                    boolean hasMainTerm = termProvider.hasMainTerm();
                    final TermService.TermProvider mainTermProvider = hasMainTerm ? termProvider.getMainTerm().get() : null;
                    boolean hasShortDescription = hasMainTerm ? mainTermProvider.hasShortDescription() : termProvider.hasShortDescription();

                    findWord = hasMainTerm ? mainTermProvider.getName() : termProvider.getName();
                    // убираем обработанный термин, чтобы не заменить его более мелким
                    content = contentMatcher.replaceAll(" ");
                }
                if(termFrequency.containsKey(findWord)) termFrequency.put(findWord,termFrequency.get(findWord)+frequency);
                else termFrequency.put(findWord,frequency);
            }
        }
        //termFrequency.entrySet().stream().forEach(System.out::println);

        //SAVE TO DB
        termFrequency.entrySet().stream().map(entry ->
                createTermRecordFrequency(record, entry.getKey(), entry.getValue())).forEach(t ->
                commonDao.save(TermRecordFrequency.class,t));
    }
}
