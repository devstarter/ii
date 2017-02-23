package org.ayfaar.app.utils;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.event.SysLogEvent;
import org.ayfaar.app.model.ActionEvent;
import org.ayfaar.app.services.moderation.Action;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Slf4j
public class SysLogListener {
    private final CommonDao commonDao;
//    final SysLogService sysLogService;

    @Inject
    public SysLogListener(CommonDao commonDao) {
        this.commonDao = commonDao;
    }

    @Async
    @EventListener
    private void listenForEvents(SysLogEvent event) {
        String message = String.format("Системное событие от %s: %s", event.getSource(), event.getMessage());
        final ActionEvent actionEvent = new ActionEvent();
        actionEvent.setAction(Action.SYS_EVENT);
        actionEvent.setMessage(message);
        actionEvent.setCreatedBy(-1);
        commonDao.save(actionEvent);
    }
}
