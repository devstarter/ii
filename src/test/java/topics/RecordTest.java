package topics;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Record;
import org.ayfaar.app.services.topics.TopicProvider;
import org.ayfaar.app.services.topics.TopicService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class RecordTest extends IntegrationTest {

    @Autowired
    CommonDao commonDao;
    @Autowired
    TopicService topicService;

    Map<String, String> audioUrls;

    @Test
    public void saveAndCreateLink() throws Exception {

        Map<String, String> parseAudio = FilesExcelParser.parse();
        Map<String, String> parse = TopicsExcelParser.parse();
        List<RecordCodes> recordCodes = RecordsExcelParser.parse();

        //GET AUDIO_URLS FOR RECORDS
        audioUrls = createAudioUrls(parseAudio);

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
        audioUrls.keySet().stream().filter(s ->
                s.equals(recordCode.getCode())).forEach(s -> record.setAudioUrl(audioUrls.get(s)));
        record.setName(name);
        record.setCode(recordCode.getCode());
        record.setCreatedAt(new Date());
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(recordCode.getCode().substring(0,10));
            record.setRecorderAt(date);
        } catch (ParseException e) {
            log.error("Date is not parse ", e);
        }
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

    private Map<String,String> createAudioUrls(Map<String,String> parseAudio){
        Map<String, String> urlAudioRecords = new HashedMap();
        for (Map.Entry<String, String> stringStringEntry : parseAudio.entrySet()) {
            String nameFile = stringStringEntry.getKey();
            String code = stringStringEntry.getKey();
            if(code.contains(".mp3")&&code.length()==16) code = new StringBuilder(code).insert(code.length()-5,0).toString();
            if(code.contains(".mp3")&&code.length()==14) code = new StringBuilder(code).insert(code.length()-4,"_01").toString();
            if (code.contains(".mp3")&&code.substring(0,1).equals("2")) {
                String url = "http://ayfaar.org/media/k2/attachments/" + code.substring(0, 4) + "/" + nameFile;
                urlAudioRecords.put(code.substring(0,code.length()-4),url);
            }
        }
        return urlAudioRecords;
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