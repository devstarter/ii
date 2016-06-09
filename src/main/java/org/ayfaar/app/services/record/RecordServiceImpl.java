package org.ayfaar.app.services.record;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.RecordDao;
import org.ayfaar.app.model.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component()
public class RecordServiceImpl implements RecordService {
    @Autowired
    CommonDao commonDao;
    @Autowired
    RecordDao recordDao;

    List<Record> allRecords;

    @PostConstruct
    private void init() {
        log.info("Records loading...");

        allRecords = commonDao.getAll(Record.class);

        log.info("Records loaded");
    }

    @Override
    public void reload() {
        init();
    }

    @Override
    public Map<String, String> getAllUriNames() {
        return allRecords.stream().collect(Collectors.toMap(record ->
                record.getUri(),record -> record.getName()));
    }

    @Override
    public Map<String, String> getAllUriCodes() {
        return allRecords.stream().collect(Collectors.toMap(record ->
                record.getUri(),record -> record.getCode()));
    }
}
