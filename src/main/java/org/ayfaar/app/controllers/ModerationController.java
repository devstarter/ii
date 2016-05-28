package org.ayfaar.app.controllers;

import one.util.streamex.StreamEx;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.ActionLog;
import org.ayfaar.app.model.PendingAction;
import org.ayfaar.app.model.User;
import org.ayfaar.app.services.moderation.ModerationService;
import org.ayfaar.app.services.user.UserPresentation;
import org.ayfaar.app.services.user.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

import static java.util.Comparator.comparingInt;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("api/moderation")
@PreAuthorize("authenticated")
public class ModerationController {
    private final CommonDao commonDao;
    private final ModerationService service;
    private final UserService userService;

    @Inject
    public ModerationController(ModerationService service, CommonDao commonDao, UserService userService) {
        this.service = service;
        this.commonDao = commonDao;
        this.userService = userService;
    }

    @RequestMapping("pending_actions")
    public List<PendingActionPresentation> getPendingActions(@AuthenticationPrincipal User currentUser) {
        // show only my users (this user can be linked with another as children for personal moderation)
        return StreamEx.of(commonDao.getList(PendingAction.class, "confirmedBy", null))
                .filter(a -> AuthController.getCurrentAccessLevel().accept(a.getAction().getRequiredAccessLevel())
                        || Objects.equals(currentUser.getId(), a.getInitiatedBy()))
                .reverseSorted(comparingInt(PendingAction::getId))
                .map(PendingActionPresentation::new)
                .map(presentation -> {
                    presentation.owner = Objects.equals(currentUser.getId(), presentation.initiatedBy.id);
                    return presentation;
                })
                .toList();
    }

    @RequestMapping("last_actions")
    public List<ActionLog> getLastActions(@PageableDefault(size = 3, sort = "createdAt", direction = DESC) Pageable pageable) {
        // организовать постраничную выдачу ActionLog, сначала выдавать самые последние
        return commonDao.getPage(ActionLog.class, pageable);
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
        public Boolean owner; // is this action created by current user

        public PendingActionPresentation(PendingAction action) {
            id = action.getId();
            text = action.getMessage();
            initiatedBy = userService.getPresentation(action.getInitiatedBy());
        }
    }
}
