package topics;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Record;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import java.io.File;
import java.util.Optional;


@Slf4j
public class WordParserTest extends IntegrationTest {

    @Autowired
    CommonDao commonDao;
    @Autowired
    ResourceLoader resourceLoader;

    @Test
    public void parse() throws Exception {
        File folder = resourceLoader.getResource("classpath:topics").getFile();
        File[] files = folder.listFiles();
        for (File file : files) {
            String url = file.getName();

            if (url.endsWith(".doc")) {
                String text = WordParser.parseWord(url);
                String code = url.substring(0, url.length() - 4);
                saveTextToRecord(text, code);
            }else if (url.endsWith(".docx")) {
                String text = WordParser.parseWordX(url);
                String code = url.substring(0, url.length() - 5);
                saveTextToRecord(text, code);
            }
        }
    }

    private void saveTextToRecord(String text, String code) {
        Optional<Record> record = commonDao.getOpt(Record.class, "code", code);
        if (record.isPresent()&&record.get().getText() == null) {
            record.get().setText(text);
            commonDao.save(record.get());
            log.info("Create text for record " + code);

        }
    }

}
