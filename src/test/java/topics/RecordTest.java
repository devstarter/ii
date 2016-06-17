package topics;

import com.google.api.services.drive.model.File;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.apache.commons.collections.map.HashedMap;
import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Record;
import org.ayfaar.app.services.topics.TopicProvider;
import org.ayfaar.app.services.topics.TopicService;
import org.ayfaar.app.utils.GoogleService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@ActiveProfiles("remote")
public class RecordTest extends IntegrationTest {

    @Autowired
    CommonDao commonDao;
    @Autowired
    TopicService topicService;
    @Autowired
    GoogleService googleService;

    Map<String, String> audioUrls;
    Map<String, String> publicRecords;

    String baseUrl = "http://ayfaar.org/media/k2/attachments/";
    int baseUrlWithYearSize = 44;
    private Map<String, Record> allSavedRecords;

    @Test
    public void saveAndCreateLink() throws Exception {
        log.info("Get public records...");
        publicRecords = FilesExcelParser.parse();                      //from files.xls
        log.info("Get code-topics map...");
        Map<String, String> codeTopicMap = TopicsExcelParser.parse();      //from Классификатор методики МИЦИАР.xlsx
        log.info("Get record-codes map...");
        List<RecordCodes> recordCodes = RecordsExcelParser.parse(); //from 2014-06-10_1, 2005-2013.xlsx

        //GET AUDIO_URLS FOR RECORDS
        log.info("Create audio urls...");
        audioUrls = createAudioUrls(publicRecords);
        log.info("Done");

        //CREATE RECORDS IN DB
        log.info("Save records in DB...");
        allSavedRecords = StreamEx.of(commonDao.getAll(Record.class)).toMap(Record::getCode, Function.identity());
//        recordCodes.stream().forEach(this::saveRecords); // --> 1

        //if code is not found in list recordCodes -> create records from this codes/urls
//        log.info("Save missed public records in DB");
//        addMissedRecords(audioUrls, recordCodes); // --> 2

        /*//CREATE LINKS
        log.info("Create links");
        for (RecordCodes recordCode : recordCodes) {
            for (Map.Entry<String, String> stringStringEntry : codeTopicMap.entrySet()) {
                if (recordCode.getTopicCods() != null) {
                    for (String s : recordCode.getTopicCods()) {
                        if (s.equals(stringStringEntry.getKey())){
                           if(allSavedRecords.containsKey(recordCode.getCode()))
                               topicService
                                       .getByName(stringStringEntry.getValue())
                                       .link(allSavedRecords.get(recordCode.getCode())); // --> 3
                        }
                    }
                }
            }
        }*/


        //ЕСЛИ необходимо ТОЛЬКО ДОКАЧАТЬ в ГУГЛ-ДРАЙВ закомментировать строки --> 1,2,3 ))))))
        audioUrls.entrySet().forEach(codeUrlEntry -> uploadNewAudioToGDrive(codeUrlEntry.getKey(), codeUrlEntry.getValue())); // --> 4
    }

    private void uploadNewAudioToGDrive(String code, String url){
        if (allSavedRecords == null) allSavedRecords = StreamEx.of(commonDao.getAll(Record.class)).toMap(Record::getCode, Function.identity());
        Record record = allSavedRecords.get(code);
        if(record != null && record.getAltAudioGid() == null){
            File file;
            try {
                new URL(url).openStream();
            } catch (IOException e) {
                log.warn("Url {} not accessible",url);
                record.setAudioUrl(null);
                commonDao.save(record);
                return;
            }
            /*try {
                file = googleService.uploadToGoogleDrive(url, code);
                if (file == null) return;
                record.setAltAudioGid(file.getId());
                commonDao.save(record);
            } catch (Exception e) {
                log.warn("File uploading error", e);
            }*/
        }
    }

    private void addMissedRecords(Map<String, String> audioUrls, List<RecordCodes> recordCodes) {
        List<String> codes = recordCodes.stream().map(RecordCodes::getCode).collect(Collectors.toList());
        audioUrls.keySet().stream()
                .filter(s -> !codes.contains(s))
                .forEachOrdered(s -> {
                    RecordCodes recordCodes1 = new RecordCodes();
                    recordCodes1.setName(publicRecords.get(audioUrls.get(s).substring(baseUrlWithYearSize)));
                    recordCodes1.setCode(s);
                    saveRecords(recordCodes1);
                });
    }

    private void saveRecords(RecordCodes recordCode) {
        if (!allSavedRecords.containsKey(recordCode.getCode())) {
            Record record = new Record();
            String name = recordCode.getName();

            record.setAudioUrl(audioUrls.get(recordCode.getCode()));
            record.setName(name);
            record.setCode(recordCode.getCode());
            record.setCreatedAt(new Date());
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = format.parse(recordCode.getCode().substring(0, 10));
                record.setRecorderAt(date);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            commonDao.save(record);
            allSavedRecords.put(record.getCode(), record);
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
            if (code.contains(".mp3")&&nameFile.length() > 12) {
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