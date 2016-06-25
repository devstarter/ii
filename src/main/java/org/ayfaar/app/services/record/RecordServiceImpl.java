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
        final boolean internalRecordAllowed = isInternalRecordAllowed();
        return allRecords.stream()
                .filter(r -> internalRecordAllowed || !StringUtils.isEmpty(r.getAudioUrl()))
                .collect(Collectors.toMap(UID::getUri, Record::getName));
    }

    @Override
    public Map<String, String> getAllUriCodes() {
        final boolean internalRecordAllowed = isInternalRecordAllowed();
        return allRecords.stream()
                .filter(r -> internalRecordAllowed || !StringUtils.isEmpty(r.getAudioUrl()))
                .collect(Collectors.toMap(UID::getUri, Record::getCode));
    }

    @Override
    public boolean isInternalRecordAllowed() {
        final Optional<User> currentUserOpt = currentUserProvider.get();
        return currentUserOpt.isPresent() && currentUserOpt.get().getRole().accept(UserRole.ROLE_EDITOR);
    }
}
