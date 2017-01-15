package org.ayfaar.app.services.record;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Record;
import org.ayfaar.app.model.UID;
import org.ayfaar.app.model.User;
import org.ayfaar.app.services.moderation.UserRole;
import org.ayfaar.app.utils.CurrentUserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.ayfaar.app.utils.StreamUtils.single;

@Slf4j
@Component()
public class RecordServiceImpl implements RecordService {
    private final CommonDao commonDao;
    private CurrentUserProvider currentUserProvider;

    private List<Record> allRecords;

    @Autowired
    public RecordServiceImpl(CommonDao commonDao, CurrentUserProvider currentUserProvider) {
        this.commonDao = commonDao;
        this.currentUserProvider = currentUserProvider;
    }

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
        final boolean internalRecordAllowed = isPrivateRecordsVisible();
        return allRecords.stream()
                .filter(r -> internalRecordAllowed || !StringUtils.isEmpty(r.getAudioUrl()))
                .collect(Collectors.toMap(UID::getUri, Record::getName));
    }

    @Override
    public Map<String, String> getAllUriCodes() {
        final boolean privateRecordsVisible = isPrivateRecordsVisible();
        return allRecords.stream()
                .filter(r -> privateRecordsVisible || !StringUtils.isEmpty(r.getAudioUrl()))
                .collect(Collectors.toMap(UID::getUri, Record::getCode));
    }

    @Override
    public Optional<Record> getByCode(String code) {
        return allRecords.stream().filter(record -> record.getCode().equals(code)).collect(single());
    }

    @Override
    public void save(Record record) {
        commonDao.save(record);
    }

    @Override
    public List<Record> getAll() {
        return allRecords;
    }

    @Override
    public boolean isPrivateRecordsVisible() {
        final Optional<User> currentUserOpt = currentUserProvider.get();
        return currentUserOpt.isPresent() && currentUserOpt.get().getRole().accept(UserRole.ROLE_EDITOR);
    }

    @Override
    public List<Record> getAll() {
        return allRecords;
    }

    @Override
    public Record save(Record record) {
        return commonDao.save(record);
    }
}
