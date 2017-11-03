package org.ayfaar.app.controllers;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.annotations.Moderated;
import org.ayfaar.app.dao.RecordDao;
import org.ayfaar.app.event.EventPublisher;
import org.ayfaar.app.event.RecordRenamedEvent;
import org.ayfaar.app.model.Record;
import org.ayfaar.app.model.User;
import org.ayfaar.app.services.moderation.Action;
import org.ayfaar.app.services.moderation.ModerationService;
import org.ayfaar.app.services.moderation.UserRole;
import org.ayfaar.app.services.topics.TopicProvider;
import org.ayfaar.app.services.topics.TopicService;
import org.ayfaar.app.sync.RecordSynchronizer;
import org.ayfaar.app.utils.Transliterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("api/record")
@Slf4j
public class RecordController {

    private final TopicService topicService;
    private final ModerationService moderationService;
    @Autowired(required = false) private RecordSynchronizer recordSynchronizer;
    private EventPublisher publisher;
    private final RecordDao recordDao;

    @Inject
    public RecordController(RecordDao recordDao, TopicService topicService, ModerationService moderationService, EventPublisher publisher) {
        this.recordDao = recordDao;
        this.topicService = topicService;
        this.moderationService = moderationService;
        this.publisher = publisher;
    }

    @RequestMapping()
    public List<Map<String, Object>> get(@RequestParam(required = false) String nameOrCode,
                                         @RequestParam(required = false) String year,
                                         @RequestParam(required = false) Record.Kind kind,
                                         @RequestParam(required = false) Boolean with_url,
                                         @AuthenticationPrincipal User currentUser,
                                         @PageableDefault(sort = "recorderAt", direction = DESC, size = 30) Pageable pageable) {
        with_url = with_url != null
                ? with_url
                : currentUser == null || !currentUser.getRole().accept(UserRole.ROLE_EDITOR);
        List<Record> records = recordDao.get(nameOrCode, year, kind, with_url, pageable);
        return records.stream().map(this::getRecordsInfo).collect(Collectors.toList());
    }

    private  Map<String, Object> getRecordsInfo(Record record) {
        Map<String, Object> recordsInfoMap = new HashMap<>();
        recordsInfoMap.put("code",record.getCode());
        recordsInfoMap.put("name",record.getName());
        recordsInfoMap.put("recorder_at",new SimpleDateFormat("yyyy-MM-dd").format(record.getRecorderAt()));
        recordsInfoMap.put("url",record.getAudioUrl());
        recordsInfoMap.put("uri",record.getUri());
        recordsInfoMap.put("duration",record.getDuration());

        List<String> topicUris = topicService.getAllLinkedWith(record.getUri())
                .map(TopicProvider::name)
                .collect(Collectors.toList());
        recordsInfoMap.put("topics", topicUris);
        return recordsInfoMap;
    }

    @RequestMapping(value = "{code}/rename", method = RequestMethod.POST)
    @Moderated(value = Action.RECORD_RENAME, command = "@recordController.rename")
    public void rename(@PathVariable String code, @RequestParam String name) {
        final Record record = recordDao.get("code", code);
        if (record == null) throw new RuntimeException("Record "+code+" not found");

        record.setPreviousName(record.getName());
        record.setName(name);
        recordDao.save(record);
        moderationService.notice(Action.RECORD_RENAMED, record.getUri(), record.getPreviousName(), record.getName());
        publisher.publishEvent(new RecordRenamedEvent(record));
    }

    @RequestMapping(value = "{code}/download/{any}", method = RequestMethod.GET)
    public void download(@PathVariable String code, HttpServletResponse response) throws IOException{
        final Record record = recordDao.get("code", code);
        if (record == null) throw new RuntimeException("Record "+code+" not found");

        final String url = record.getAudioUrl();
        if (url == null) throw new RuntimeException("Has no download url for record");

        final int bufferSize = 4096;
        final String mimeType = "audio/mpeg";
        String name = Transliterator.transliterate(record.getName()).replace("\"", "");
        String headerValue = String.format("attachment;");

        InputStream  inputStream = new URL(url).openStream();

        response.setContentType(mimeType);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, headerValue);

        OutputStream outputStream = response.getOutputStream();
        byte[] buffer = new byte[bufferSize];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        outputStream.close();
//        return "redirect:" + url;
    }

    @RequestMapping("sync")
    public void sync() throws IOException {
        recordSynchronizer.synchronize();
    }
}
