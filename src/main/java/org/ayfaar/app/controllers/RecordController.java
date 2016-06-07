package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.RecordDao;
import org.ayfaar.app.model.Record;
import org.ayfaar.app.model.User;
import org.ayfaar.app.services.moderation.UserRole;
import org.ayfaar.app.services.record.RecordService;
import org.ayfaar.app.services.topics.TopicService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/record")
public class RecordController {

    @Inject
    TopicService topicService;
    @Inject
    RecordDao recordDao;

    @RequestMapping()
    public List<Map<String, Object>> get(@RequestParam(required = false) String name,
                                             @RequestParam(required = false) String year,
                                             @RequestParam(required = false, defaultValue = "true") boolean with_url,
                                             @PageableDefault Pageable pageable) {
        Optional<User> currentUser = AuthController.getCurrentUser();
        if (currentUser.isPresent() && currentUser.get().getRole() == UserRole.ROLE_EDITOR) with_url = false;
        List<Record> records = recordDao.get(name, year, with_url, pageable);
        return records.stream().map(record -> getRecordsInfo(record)).collect(Collectors.toList());
    }

    private  Map<String, Object> getRecordsInfo(Record record) {
        Map<String, Object> recordsInfoMap = new HashMap<>();
        recordsInfoMap.put("code",record.getCode());
        recordsInfoMap.put("name",record.getName());
        recordsInfoMap.put("recorder_at",new SimpleDateFormat("yyyy-MM-dd").format(record.getRecorderAt()));
        recordsInfoMap.put("url",record.getAudioUrl());
        recordsInfoMap.put("uri",record.getUri());

        List<String> topicsUri = topicService.getAllTopicsLinkedWithUri(record.getUri()).stream().map(topicProvider ->
                topicProvider.topic().getUri()).collect(Collectors.toList());
        recordsInfoMap.put("topics",topicsUri);
        return recordsInfoMap;
    }
}
