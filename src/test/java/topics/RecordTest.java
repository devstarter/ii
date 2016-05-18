package topics;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Record;
import org.ayfaar.app.model.RecordCodes;
import org.ayfaar.app.services.topics.TopicProvider;
import org.ayfaar.app.services.topics.TopicService;
import org.ayfaar.app.utils.RecordService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;

public class RecordTest extends IntegrationTest {

    @Autowired
    CommonDao commonDao;
    @Autowired
    RecordService recordService;
    @Autowired
    TopicService topicService;

    @Test
    public void saveAndCreateLink() throws Exception {

        Map<String, String> parse = TopicsExcelParser.parse();
        List<RecordCodes> recordCodes = RecordsExcelParser.parse();

        //CREATE RECORDS IN DB
        recordService.saveToDb(recordCodes);

        //CREATE LINKS
        for (RecordCodes recordCode : recordCodes) {
            for (Map.Entry<String, String> stringStringEntry : parse.entrySet()) {
                if (recordCode.getTopicCods()!=null) {

                    for (String s : recordCode.getTopicCods()) {
                        if (s.equals(stringStringEntry.getKey()))
                            recordService.createLink(recordCode.getCode(), stringStringEntry.getValue());
                    }
                }
            }
        }
    }

    @Test
    public void getRecords() {
        TopicProvider topicProvider = topicService.getByName("Звёздные Имена");
        TopicProvider.TopicResources resources = topicProvider.resources();
        List<TopicProvider.ResourcePresentation> record = resources.record;
        for (TopicProvider.ResourcePresentation resourcePresentation : record) {
            Record resource = (Record)resourcePresentation.resource;
            String name = resource.getName();
            System.out.println(name);
        }
    }


}