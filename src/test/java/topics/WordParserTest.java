package topics;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Record;
import org.ayfaar.app.model.TermRecordFrequency;
import org.ayfaar.app.services.record.RecordService;
import org.ayfaar.app.utils.TermService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;


@Slf4j
public class WordParserTest extends IntegrationTest {

    @Autowired
    CommonDao commonDao;
    @Autowired
    ResourceLoader resourceLoader;
    @Autowired
    RecordService recordService;
    @Autowired
    TermService termService;

    @Test
    public void parse() throws Exception {
        File folder = resourceLoader.getResource("classpath:topics").getFile();
        File[] files = folder.listFiles();
        for (File file : files) {
            String url = file.getName();

            if (url.endsWith(".doc")) {
                String text = WordParser.parseWord(url);
                String code = url.substring(0, url.length() - 4);
                termRecordFrequency(code,text);
                log.info("Parse " + url);
                //saveTextToRecord(text, code);
            }else if (url.endsWith(".docx")) {
                String text = WordParser.parseWordX(url);
                String code = url.substring(0, url.length() - 5);
                termRecordFrequency(code,text);
                log.info("Parse " + url);
                //saveTextToRecord(text, code);
            }
        }
    }

    @Test
    public void getRecordsWithTermTest(){
        recordService.getRecordsWithTerm("днк").stream().forEach(System.out::println);
    }

    @Test
    private void getByFrequencyTest() {
        List<TermRecordFrequency> byFrequency = recordService.getByFrequency(5);
        byFrequency.stream().map(TermRecordFrequency::getTerm).forEach(System.out::println);
    }

    private void termRecordFrequency(String record, String text) {
        if (text == null || text.isEmpty()) return;
        for (Map.Entry<String, TermService.TermProvider> entry : termService.getAll()) {
            String term = entry.getKey();

            Pattern pattern = compile("(([^A-Za-zА-Яа-я0-9Ёё\\[\\|])|^)(около|слабо|высоко|не|анти|разно|дву|трёх|четырёх|пяти|шести|семи|восьми|девяти|десяти|внутри|пост|меж|мощно|взаимо|внутри|не)?("
                    + term + ")(([^A-Za-zА-Яа-я0-9Ёё\\]\\|])|$)", UNICODE_CHARACTER_CLASS | UNICODE_CASE | CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);

            int frequency = 0;
            boolean find = false;

            while (matcher.find()) {
                find = true;
                frequency++;
            }
            if (find) {
                TermRecordFrequency termRecordFrequency = new TermRecordFrequency();
                termRecordFrequency.setRecord(record);
                termRecordFrequency.setTerm(term);
                termRecordFrequency.setFrequency(frequency);

                commonDao.save(TermRecordFrequency.class, termRecordFrequency);
                log.info("Save term = " + term + " with frequency = " + frequency);
            }
        }
    }


    //FOR SAVE ALL TEXT TO RECORDS_TABLE IN DB
    private void saveTextToRecord(String text, String code) {
        Optional<Record> record = commonDao.getOpt(Record.class, "code", code);
        if (record.isPresent()&&record.get().getText() == null) {
            record.get().setText(text);
            commonDao.save(record.get());
            log.info("Create text for record " + code);

        }
    }

}
