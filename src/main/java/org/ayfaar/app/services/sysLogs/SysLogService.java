package org.ayfaar.app.services.sysLogs;

import org.ayfaar.app.event.SysLogEvent;
import org.ayfaar.app.model.SysLog;

import java.util.List;

public interface SysLogService {
    List<SysLog> getAll();
    SysLog save(SysLogEvent event);
}
