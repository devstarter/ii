package topics;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Record;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import java.io.File;
import java.util.Map;
import java.util.Optional;



@Slf4j
public class WordParserTest extends IntegrationTest {

    @Autowired
    CommonDao commonDao;
    @Autowired
    ResourceLoader resourceLoader;

    @Test
    public void parseTest() throws Exception {
        File folder = resourceLoader.getResource("classpath:topics").getFile();
        Map<String, String> recordsWithText = WordParserService.parse(folder);
        recordsWithText.entrySet().stream().forEach(System.out::println);
        //recordsWithText.entrySet().stream().forEach(e -> saveTextToRecord(e.getKey(),e.getValue()));
    }

    //FOR SAVE ALL TEXT TO RECORDS_TABLE IN DB
    private void saveTextToRecord(String code, String text) {
        Optional<Record> record = commonDao.getOpt(Record.class, "code", code);
        if (record.isPresent()&&record.get().getText() == null) {
            record.get().setText(text);
            commonDao.save(record.get());
            log.info("Create text for record " + code);

        }
    }

}
