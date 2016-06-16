package org.ayfaar.app.services.record;

import org.ayfaar.app.model.TermRecordFrequency;
import java.util.List;
import java.util.Map;

public interface RecordService {

    void reload();

    Map<String, String> getAllUriNames();
    Map<String, String> getAllUriCodes();

    List<String> getRecordsWithTerm(String term);

    List<TermRecordFrequency> getByFrequency();
}
