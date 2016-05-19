package topics;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Record;
import org.ayfaar.app.services.topics.TopicProvider;
import org.ayfaar.app.services.topics.TopicService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RecordTest extends IntegrationTest {

    @Autowired
    CommonDao commonDao;
    @Autowired
    TopicService topicService;

    @Test
    public void saveAndCreateLink() throws Exception {

        Map<String, String> parse = TopicsExcelParser.parse();
        List<RecordCodes> recordCodes = RecordsExcelParser.parse();

        //CREATE RECORDS IN DB
        recordCodes.stream().forEach(this::saveRecords);

        //CREATE LINKS
        for (RecordCodes recordCode : recordCodes) {
            for (Map.Entry<String, String> stringStringEntry : parse.entrySet()) {
                if (recordCode.getTopicCods()!=null) {
                    for (String s : recordCode.getTopicCods()) {
                        Optional<Record> record = null;
                        if (s.equals(stringStringEntry.getKey())){
                            record = commonDao.getOpt(Record.class, "code", recordCode.getCode());
                            topicService.getByName(stringStringEntry.getValue()).link(record.get());
                        }
                    }
                }
            }
        }
    }

    private void saveRecords(RecordCodes recordCode) {
        Record record = new Record();
        String name = recordCode.getName();
        if (name.length()>254) {
            name = name.substring(0, 247);
        }
        record.setName(name);
        record.setCode(recordCode.getCode());
        record.setCreatedAt(new Date());
        record.setRecorderAt(recordCode.getCode().substring(0,10));
        commonDao.save(record);
    }

    @Test
    public void getRecords() {
        System.out.println("GET RECORDS FOR TOPIC - Звёздные Имена");

        TopicProvider topicProvider = topicService.getByName("Звёздные Имена");
        topicProvider.resources().record.stream()
                .map(r -> ((Record) r.resource).getName())
                .forEach(System.out::println);

    }

//    GET RECORDS FOR TOPIC - Звёздные Имена
//    Влияние значения тоновых имен.
//    Звёздное имя это космический код ССМИИЙСМАА-А, которые отражают определённые аспекты качеств и могут принадлежать разным космическим духам.
//    О тоновых именах как о проекции различных цивилизаций.
//    К слову о Духовных именах.
//    Что такое Звёздные аллели.
//    О тоновых или Звёздных именах. О проявлении нас в 4й мерности. Об уровнях допуска при глубинных медитациях.
//    Что такое "Звёздное имя". О звуковых Космических Кодах.
//    О последствии синтеза и тоновых именах.
//    Об именах.
//    Каждый из нас является неким аспектом другого. О трансгрессии в структуре ЛЛУУ-ВВУ. О привязке Звёздного Имени к диапазону проявления.
//    О получении детьми Звездного Имени.
//    Про двойные имена.
//            О "Звёздных именах" и влиянии Системных Кодов на конфигурации самосознания при смене имени.
//    Пример понимания переводов тоновых имен.
//    Тоновые имена их влияние на нашу жизнь.
//    Пример получения Орисом тоновых имен.

}