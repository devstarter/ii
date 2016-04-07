package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.User;
import org.ayfaar.app.utils.exceptions.Exceptions;
import org.ayfaar.app.utils.exceptions.LogicalException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.inject.Inject;
import java.util.List;
import static org.ayfaar.app.services.moderation.AccessLevel.fromPrecedence;

@RestController
@RequestMapping("api/user")
public class UserController {

    @Inject
    CommonDao commonDao;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping("users")
    public List<User> getAll(@PageableDefault Pageable pageable) {
        return commonDao.getPage(User.class, pageable);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping("users/{email}")
    public User getUserDetail(@PathVariable String email) {
        return commonDao.getOpt(User.class, email).orElseThrow(() -> new LogicalException(Exceptions.EMAIL_NOT_FOUND, email));
    }

    @Secured("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "userRole", method = RequestMethod.POST) //0 - ADMIN, 1 - EDITOR, 2 - AUTHENTICATED
    public void setRoleByEmail(@RequestParam String email, @RequestParam int numRole){
        User user = commonDao.getOpt(User.class, email).orElseThrow(() -> new LogicalException(Exceptions.EMAIL_NOT_FOUND, email));
        fromPrecedence(numRole).ifPresent(accessLevel -> user.setRole(accessLevel));
        commonDao.save(user);
    }
}
