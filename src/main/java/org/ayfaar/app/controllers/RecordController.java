package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.RecordDao;
import org.ayfaar.app.model.Record;
import org.ayfaar.app.model.User;
import org.ayfaar.app.services.moderation.UserRole;
import org.ayfaar.app.services.topics.TopicProvider;
import org.ayfaar.app.services.topics.TopicService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("api/record")
public class RecordController {

    @Inject
    TopicService topicService;
    @Inject
    RecordDao recordDao;

    @RequestMapping()
    public List<Map<String, Object>> get(@RequestParam(required = false) String nameOrCode,
                                             @RequestParam(required = false) String year,
                                             @RequestParam(required = false) Boolean with_url,
                                             @AuthenticationPrincipal User currentUser,
                                             @PageableDefault(sort = "recorderAt", direction = DESC) Pageable pageable) {

        with_url = with_url != null ? with_url : !currentUser.getRole().accept(UserRole.ROLE_EDITOR);
        List<Record> records = recordDao.get(nameOrCode, year, with_url, pageable);
        return records.stream().map(this::getRecordsInfo).collect(Collectors.toList());
    }

    private  Map<String, Object> getRecordsInfo(Record record) {
        Map<String, Object> recordsInfoMap = new HashMap<>();
        recordsInfoMap.put("code",record.getCode());
        recordsInfoMap.put("name",record.getName());
        recordsInfoMap.put("recorder_at",new SimpleDateFormat("yyyy-MM-dd").format(record.getRecorderAt()));
        recordsInfoMap.put("url",record.getAudioUrl());
        recordsInfoMap.put("uri",record.getUri());

        List<String> topicsUri = topicService.getAllTopicsLinkedWithUri(record.getUri()).stream().map(TopicProvider::uri).collect(Collectors.toList());
        recordsInfoMap.put("topics",topicsUri);
        return recordsInfoMap;
    }
}
