package org.ayfaar.app.services.record;

import java.util.Map;

public interface RecordService {

    void reload();

    Map<String, String> getAllUriNames();

    boolean isInternalRecordAllowed();

    Map<String, String> getAllUriCodes();
}
