package topics;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.TermRecordFrequency;
import org.ayfaar.app.services.record.RecordService;
import org.ayfaar.app.utils.TermService;
import org.ayfaar.app.utils.TermsFinder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import java.io.File;
import java.util.List;
import java.util.Map;




@Slf4j
public class TermRecordsTest extends IntegrationTest {

    @Autowired
    TermsFinder termsFinder;
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

    private TermRecordFrequency                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 createTermRecordFrequency(String record, String term, int frequency){
        TermRecordFrequency termRecordFrequency = new TermRecordFrequency();
        termRecordFrequency.setRecord(record);
        termRecordFrequency.setTerm(term);
        termRecordFrequency.setFrequency(frequency);
        return termRecordFrequency;
    }

    private void termRecordFrequency(String record, String content) {

        Map<String, Integer> termFrequency = termsFinder.getTermsWithFrequency(content);
        termFrequency.entrySet().stream().forEach(System.out::println);

        //SAVE TO DB
//        termFrequency.entrySet().stream().map(entry ->
//                createTermRecordFrequency(record, entry.getKey(), entry.getValue())).forEach(t ->
//                commonDao.save(TermRecordFrequency.class,t));
    }
}
