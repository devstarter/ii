package org.ayfaar.app.services.record;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.RecordDao;
import org.ayfaar.app.model.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.stream.Collectors;

@Component()
public class RecordServiceImpl implements RecordService {
    @Autowired
    CommonDao commonDao;
    @Autowired
    RecordDao recordDao;

    @Override
    public Map<String, String> getAllUriNames() {
        return commonDao.getAll(Record.class).stream().collect(Collectors.toMap(record ->
                record.getUri(),record -> record.getName()));
    }

    @Override
    public Map<String, String> getAllUriCodes() {
        return commonDao.getAll(Record.class).stream().collect(Collectors.toMap(record ->
                record.getUri(),record -> record.getCode()));
    }
}
