package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.User;
import org.ayfaar.app.services.moderation.Action;
import org.ayfaar.app.services.moderation.ModerationService;
import org.ayfaar.app.services.moderation.UserRole;
import org.ayfaar.app.utils.exceptions.ExceptionCode;
import org.ayfaar.app.utils.exceptions.LogicalException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import javax.inject.Inject;
import java.util.List;


@RestController
@RequestMapping("api/user")
public class UserController {
    @Inject CommonDao commonDao;
    @Inject ModerationService moderationService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping
    public List<User> getAll(@PageableDefault Pageable pageable) {
        return commonDao.getPage(User.class, pageable);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping("{email}")
    public User getUserDetail(@PathVariable String email) {
        return commonDao.getOpt(User.class, email).orElseThrow(() -> new LogicalException(ExceptionCode.USER_NOT_FOUND, email));
    }

    @Secured("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "update-role", method = RequestMethod.POST)
    public void updateRole(@RequestParam String email, @RequestParam int numRole){
        User user = commonDao.getOpt(User.class, email).orElseThrow(() -> new LogicalException(ExceptionCode.USER_NOT_FOUND, email));
        final UserRole accessLevel = UserRole.fromPrecedence(numRole)
                .orElseThrow(() -> new LogicalException(ExceptionCode.ROLE_NOT_FOUND, numRole));
        user.setRole(accessLevel);
        commonDao.save(user);
    }

    @RequestMapping("current")
    public User getCurrent(@AuthenticationPrincipal User current){
        return current;
    }

    @RequestMapping(value = "current/rename", method = RequestMethod.POST)
    @Secured("authenticated")
    public User renameCurrent(@AuthenticationPrincipal User current, @RequestParam String name){
        final String oldName = current.getName();
        current.setName(name);
        commonDao.save(current);
        moderationService.notice(Action.USER_RENAME, oldName, name);
        return current;
    }

    @RequestMapping(value = "hide-actions-before/{id}", method = RequestMethod.POST)
    public void hideActions(@AuthenticationPrincipal User user, @PathVariable Integer id) {
        user.setHiddenActionEventId(id);
        commonDao.save(user);
    }
}
