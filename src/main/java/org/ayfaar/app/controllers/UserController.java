package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.User;
import org.ayfaar.app.services.moderation.UserRole;
import org.ayfaar.app.utils.exceptions.Exceptions;
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

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping
    public List<User> getAll(@PageableDefault Pageable pageable) {
        return commonDao.getPage(User.class, pageable);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping("{email}")
    public User getUserDetail(@PathVariable String email) {
        return commonDao.getOpt(User.class, email).orElseThrow(() -> new LogicalException(Exceptions.USER_NOT_FOUND, email));
    }

    @Secured("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "update-role", method = RequestMethod.POST)
    public void updateRole(@RequestParam String email, @RequestParam int numRole){
        User user = commonDao.getOpt(User.class, email).orElseThrow(() -> new LogicalException(Exceptions.USER_NOT_FOUND, email));
        final UserRole accessLevel = UserRole.fromPrecedence(numRole)
                .orElseThrow(() -> new LogicalException(Exceptions.ROLE_NOT_FOUND, numRole));
        user.setRole(accessLevel);
        commonDao.save(user);
    }

    @RequestMapping("current")
    public User getCurrent(@AuthenticationPrincipal User current){
        return current;
    }

    @RequestMapping("current/rename")
    @Secured("authenticated")
    public User renameCurrent(@AuthenticationPrincipal User current, @RequestParam String name){
        current.setName(name);
        commonDao.save(current);
        return current;
    }
}
