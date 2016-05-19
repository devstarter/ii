package org.ayfaar.app.controllers;

import lombok.Builder;
import one.util.streamex.StreamEx;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.ActionLog;
import org.ayfaar.app.model.PendingAction;
import org.ayfaar.app.services.moderation.ModerationService;
import org.ayfaar.app.services.user.UserPresentation;
import org.ayfaar.app.services.user.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

import static java.util.Comparator.comparingInt;

@RestController
@RequestMapping("api/moderation")
@PreAuthorize("authenticated")
public class ModerationController {
    @Inject CommonDao commonDao;
    @Inject ModerationService service;
    @Inject UserService userService;

    @RequestMapping("pending_actions")
    public List<PendingActionPresentation> getPendingActions() {
        // show only my users (this user can be linked with another as children for personal moderation)
        return StreamEx.of(commonDao.getList(PendingAction.class, "confirmedBy", null))
                .filter(a -> AuthController.getCurrentAccessLevel().accept(a.getAction().getRequiredAccessLevel()))
                .reverseSorted(comparingInt(PendingAction::getId))
                .map(PendingActionPresentation::new)
                .toList();
    }

    @RequestMapping("last_actions")
    public List<ActionLog> getLastActions() {
        // организовать постраничную выдачу ActionLog, сначала выдавать самые последние
        return null;
    }

    @RequestMapping("{id}/confirm")
    public void confirm(@PathVariable Integer id) {
        final PendingAction action = commonDao.getOpt(PendingAction.class, id).get();
        service.confirm(action);
    }

    @RequestMapping(value = "{id}/cancel", method = RequestMethod.POST)
    public void cancel(@PathVariable Integer id) {
        service.cancel(id);
    }

    private class PendingActionPresentation {
        public Integer id;
        public String text;
        public UserPresentation initiatedBy;
        public PendingActionPresentation(PendingAction action) {
            id = action.getId();
            text = action.getMessage();
            initiatedBy = userService.getPresentation(action.getInitiatedBy());
        }
    }

    @Builder
    private static class CurrentStatus {
        public List<PendingActionPresentation> pendingActions;
    }
}
