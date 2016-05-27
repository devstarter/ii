package topics;

import com.google.api.services.drive.model.File;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Record;
import org.ayfaar.app.services.topics.TopicProvider;
import org.ayfaar.app.services.topics.TopicService;
import org.ayfaar.app.utils.GoogleService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class RecordTest extends IntegrationTest {

    @Autowired
    CommonDao commonDao;
    @Autowired
    TopicService topicService;
    @Autowired
    GoogleService googleService;

    Map<String, String> audioUrls;
    Map<String, String> parseAudio;

    String baseUrl = "http://ayfaar.org/media/k2/attachments/";
    int baseUrlWithYearSize = 44;

    @Test
    public void saveAndCreateLink() throws Exception {

        parseAudio = FilesExcelParser.parse();                      //from files.xls
        Map<String, String> parse = TopicsExcelParser.parse();      //from Классификатор методики МИЦИАР.xlsx
        List<RecordCodes> recordCodes = RecordsExcelParser.parse(); //from 2014-06-10_1, 2005-2013.xlsx

        //GET AUDIO_URLS FOR RECORDS
        audioUrls = createAudioUrls(parseAudio);

        //CREATE RECORDS IN DB with Upload to GDrive
        recordCodes.stream().forEach(this::saveRecords); // --> 1

        //if code is not found in list recordCodes -> create records from this codes/urls
        saveNewRecordsFromFiles(audioUrls,recordCodes); // --> 2

        //CREATE LINKS
        for (RecordCodes recordCode : recordCodes) {
            for (Map.Entry<String, String> stringStringEntry : parse.entrySet()) {
                if (recordCode.getTopicCods()!=null) {
                    for (String s : recordCode.getTopicCods()) {
                        Optional<Record> record = null;
                        if (s.equals(stringStringEntry.getKey())){
                            record = commonDao.getOpt(Record.class, "code", recordCode.getCode());
                           if(record.isPresent())topicService.getByName(stringStringEntry.getValue()).link(record.get()); // --> 3
                        }
                    }
                }
            }
        }
        //ЕСЛИ необходимо ТОЛЬКО ДОКАЧАТЬ в ГУГЛ-ДРАЙВ закомментировать строки --> 1,2,3 ))))))
        audioUrls.entrySet().stream().forEach(stringStringEntry -> uploadNewAudioToGDrive(stringStringEntry.getKey(),stringStringEntry.getValue())); // --> 4
    }

    private void uploadNewAudioToGDrive(String code, String url){
        Optional<Record> record = commonDao.getOpt(Record.class, "code", code);
        if(record.isPresent() && record.get().getAltAudioGid() == null){
            File file = null;
            try {
                file = googleService.uploadToGoogleDrive(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (file != null) {
                record.get().setAltAudioGid(file.getId());
                commonDao.save(record.get());
                log.info("<--Upload and save new id for record " + record.get().getCode() + "-->");
            }
        }
    }

    private void saveNewRecordsFromFiles(Map<String, String> audioUrls, List<RecordCodes> recordCodes) {
        List<String> codes = recordCodes.stream().map(recordCode -> recordCode.getCode()).collect(Collectors.toList());
        for (String s : audioUrls.keySet()) {
            if (!codes.contains(s)) {
                RecordCodes recordCodes1 = new RecordCodes();
                recordCodes1.setName(parseAudio.get(audioUrls.get(s).substring(baseUrlWithYearSize)));
                recordCodes1.setCode(s);
                saveRecords(recordCodes1);
            }
        }
    }

    private void saveRecords(RecordCodes recordCode) {
        Optional<Record> recordSave = commonDao.getOpt(Record.class, "code", recordCode.getCode());
        Record record = null;
        if (!recordSave.isPresent()) {
            record = new Record();
            String name = recordCode.getName();

            record.setAudioUrl(audioUrls.get(recordCode.getCode()));
            record.setName(name);
            record.setCode(recordCode.getCode());
            record.setCreatedAt(new Date());

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = format.parse(recordCode.getCode().substring(0,10));
                record.setRecorderAt(date);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            commonDao.save(record);
        }
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
            if (code.contains(".mp3")&&nameFile.length() > 12) {//отсекаем маленькие топики с названием типа ответ.мп3 пока с ними будет ясно
                if(code.length()==16) code = new StringBuilder(code).insert(code.length()-5,0).toString(); //если в коде номера после даты меньше 10 и без нуля
                if(code.length()==14) code = new StringBuilder(code).insert(code.length()-4,"_01").toString(); //если в коде только дата без порядкового номера
                String yearForUrl = "0000"; //default
                if (code.substring(0,1).equals("2")) {
                    yearForUrl = code.substring(0, 4);
                    //if (code.substring(0,1).equals("M")) yearForUrl = code.substring(code.length()-10, code.length()-6);
                    String url = baseUrl + yearForUrl + "/" + nameFile;
                    urlAudioRecords.put(code.substring(0, code.length() - 4), url);
                }
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