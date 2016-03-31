package org.ayfaar.app.services.moderation;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.SystemEvent;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.stereotype.Service;

import javax.inject.Inject;


@Service
@Slf4j
public class ModerationService {
    private CommonDao commonDao;

    @Inject
    protected ModerationService(CommonDao commonDao) {
        this.commonDao = commonDao;
    }

    public void notice(String action, Object... args) {
        String message = MessageFormatter.arrayFormat(action, args).getMessage();
        log.info(message);
        final SystemEvent event = new SystemEvent();
        event.setMessage(message);
        commonDao.save(event);
    }

    public void confirm(Action action) {
        // currentUser has allowedActions
        // throw an exception (ConfirmationRequiredException) on user has no rights
    }

}
