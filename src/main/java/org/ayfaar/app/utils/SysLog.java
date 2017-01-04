package org.ayfaar.app.utils;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.event.SysLogEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Slf4j
public class SysLog {
    final CommonDao commonDao;

    @Inject
    public SysLog(CommonDao commonDao) {
        this.commonDao = commonDao;
    }

    @Async
    @EventListener
    private void listenForEvents(SysLogEvent event) {
        log.debug("Event received {}", event);
    }
}
