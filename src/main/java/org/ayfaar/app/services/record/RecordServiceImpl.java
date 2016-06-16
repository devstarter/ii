package org.ayfaar.app.services.record;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.RecordDao;
import org.ayfaar.app.model.Record;
import org.ayfaar.app.model.TermRecordFrequency;
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
    List<TermRecordFrequency> termRecordFrequencies;

    private static final int MAX_TERM_LOADING = 3;

    @PostConstruct
    private void init() {
        log.info("Records loading...");

        allRecords = commonDao.getAll(Record.class);

        log.info("Records loaded");

        termRecordFrequencies = commonDao.getPage(TermRecordFrequency.class, 0, MAX_TERM_LOADING, "frequency", "desc");

        log.info("Term-Record Frequencies loaded");
    }

    @Override
    public void reload() {
        init();
    }

    @Override
    public Map<String, String> getAllUriNames() {
        return allRecords.stream().collect(Collectors.toMap(Record::getUri,Record::getName));
    }

    @Override
    public Map<String, String> getAllUriCodes() {
        return allRecords.stream().collect(Collectors.toMap(Record::getUri,Record::getCode));
    }

    @Override
    public List<String> getRecordsWithTerm(String term){
        return termRecordFrequencies.stream().filter(t ->
                t.getTerm().equals(term)).map(TermRecordFrequency::getRecord).collect(Collectors.toList());
    }

    @Override
    public List<TermRecordFrequency> getByFrequency(){
        return termRecordFrequencies;
    }
}
