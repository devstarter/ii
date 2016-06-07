package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.RecordDao;
import org.ayfaar.app.model.Record;
import org.ayfaar.app.services.record.RecordService;
import org.ayfaar.app.services.topics.TopicService;
import org.springframework.web.bind.annotation.*;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/record")
public class RecordController {

    @Inject
    CommonDao commonDao;
    @Inject
    RecordService recordService;
    @Inject
    TopicService topicService;
    @Inject
    RecordDao recordDao;

    @RequestMapping()
    public List<Map<String, Object>> get(   @RequestParam(required = false) String nameOrCode,
                                            @RequestParam(required = false) String year,
                                            @RequestParam(required = false, defaultValue = "false") boolean is_url) {

        return getRecordsInfo(recordDao.get(nameOrCode, year, is_url));
    }

    private List<Map<String, Object>> getRecordsInfo(List<Record> records) {
        return records.stream().map(record -> getRecordsInfo(record)).collect(Collectors.toList());
    }

    private  Map<String, Object> getRecordsInfo(Record record) {
        Map<String, Object> recordsInfoMap = new HashMap<>();
        List<String> topics;
        recordsInfoMap.put("code",record.getCode());
        recordsInfoMap.put("name",record.getName());
        recordsInfoMap.put("recorder_at",record.getRecorderAt());
        recordsInfoMap.put("url",record.getAudioUrl());
        recordsInfoMap.put("uri",record.getUri());

        topics = topicService.getAllTopicsLinkedWithUri(record.getUri());
        recordsInfoMap.put("topics",topics);
        return recordsInfoMap;
    }
}
