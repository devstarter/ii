package org.ayfaar.app.utils;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.event.SysLogEvent;
import org.ayfaar.app.services.sysLogs.SysLogService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Slf4j
public class SysLogListener {
    final SysLogService sysLogService;

    @Inject
    public SysLogListener(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

    @Async
    @EventListener
    private void listenForEvents(SysLogEvent event) {
        log.debug("Event received {}", event);
        sysLogService.save(event);
    }
}
