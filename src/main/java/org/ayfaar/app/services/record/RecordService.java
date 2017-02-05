package org.ayfaar.app.services.record;

import java.util.Map;

public interface RecordService {

    void reload();

    Map<String, String> getAllUriNames();

    boolean isPrivateRecordsVisible();

    Map<String, String> getAllUriCodes();
}
