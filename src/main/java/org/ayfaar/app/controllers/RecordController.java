package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.model.Document;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Record;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Predicate;

import static org.ayfaar.app.utils.UriGenerator.generate;
import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("api/record")
public class RecordController {

    @Inject
    CommonDao commonDao;
    @Inject
    LinkDao linkDao;

    @RequestMapping("{code}")
    public  Map<String,Object> get(@PathVariable String code) {
        Map<String,Object> recordsInfoMap = new HashMap<>();
        List<String> topics = new ArrayList<>();
        Record record = commonDao.getOpt(Record.class, "code", code).get();
        recordsInfoMap.put("code",record.getCode());
        recordsInfoMap.put("name",record.getName());
        recordsInfoMap.put("recorder_at",record.getRecorderAt());
        recordsInfoMap.put("url",record.getAudioUrl());
        recordsInfoMap.put("uri",record.getUri());
        String uri = record.getUri();
        List<Link> allLinks = linkDao.getAllLinks(uri);
        for (Link link : allLinks) {
            topics.add(link.getUid1().getUri());
        }
        recordsInfoMap.put("topics",topics);

        return recordsInfoMap;
    }

    @RequestMapping("last")
    public List<Record> getLast(@PageableDefault(size = 9, sort = "recorderAt", direction = DESC) Pageable pageable) {
        return commonDao.getPage(Record.class, pageable);
    }

    @RequestMapping("getForUrlPresent")
    public List<Record> getForUrlPresent(@PageableDefault(size = 9, sort = "audioUrl", direction = DESC) Pageable pageable) {
        return commonDao.getPage(Record.class, pageable);
    }
}
