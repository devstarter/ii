package org.ayfaar.app.services.record;

import org.ayfaar.app.model.Record;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RecordService {

    void reload();

    Map<String, String> getAllUriNames();

    List<Record> getAll();

    boolean isPrivateRecordsVisible();

    Map<String, String> getAllUriCodes();

    Optional<Record> getByCode(String code);

    void save(Record record);
}
