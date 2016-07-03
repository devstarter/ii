package org.ayfaar.app.controllers;

import org.ayfaar.app.annotations.Moderated;
import org.ayfaar.app.dao.RecordDao;
import org.ayfaar.app.model.Record;
import org.ayfaar.app.model.User;
import org.ayfaar.app.services.moderation.Action;
import org.ayfaar.app.services.moderation.ModerationService;
import org.ayfaar.app.services.moderation.UserRole;
import org.ayfaar.app.services.topics.TopicProvider;
import org.ayfaar.app.services.topics.TopicService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("api/record")
public class RecordController {

    private final TopicService topicService;
    private final ModerationService moderationService;
    private final RecordDao recordDao;

    @Inject
    public RecordController(RecordDao recordDao, TopicService topicService, ModerationService moderationService) {
        this.recordDao = recordDao;
        this.topicService = topicService;
        this.moderationService = moderationService;
    }

    @RequestMapping()
    public List<Map<String, Object>> get(@RequestParam(required = false) String nameOrCode,
                                         @RequestParam(required = false) String year,
                                         @RequestParam(required = false) Boolean with_url,
                                         @AuthenticationPrincipal User currentUser,
                                         @PageableDefault(sort = "recorderAt", direction = DESC, size = 30) Pageable pageable) {
        with_url = with_url != null
                ? with_url
                : currentUser == null || !currentUser.getRole().accept(UserRole.ROLE_EDITOR);
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

        List<String> topicUris = topicService.getAllTopicsLinkedWith(record.getUri())
                .map(TopicProvider::name)
                .collect(Collectors.toList());
        recordsInfoMap.put("topics", topicUris);
        return recordsInfoMap;
    }

    @RequestMapping(value = "{code}/rename", method = RequestMethod.POST)
    @Moderated(value = Action.RECORD_RENAME, command = "@recordController.rename")
    public void rename(@PathVariable String code, @RequestParam String name) {
        final Record record = recordDao.get("code", code);
        if (record == null) throw new RuntimeException("Record not found");

        record.setPreviousName(record.getName());
        record.setName(name);
        recordDao.save(record);
        moderationService.notice(Action.RECORD_RENAMED, record.getUri(), record.getPreviousName(), record.getName());
    }
}
