package org.ayfaar.app.utils;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Record;
import org.ayfaar.app.model.RecordCodes;
import org.ayfaar.app.services.topics.TopicService;
import org.springframework.stereotype.Component;
import javax.inject.Inject;
import java.util.*;

@Component
@Slf4j
public class RecordService {
    @Inject
    private CommonDao commonDao;
    @Inject
    private TopicService topicService;

    public void saveToDb(List<RecordCodes> recordCodes){
        for (RecordCodes recordCode : recordCodes) {
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
    }

    public void createLink(String recordCode, String topicName){
        Optional<Record> record = commonDao.getOpt(Record.class, "code", recordCode);
        topicService.getByName(topicName).link(record.get());
    }

    private String toString(int i) {
        return i + "";
    }
}
