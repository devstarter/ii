package org.ayfaar.app.services.user;

import org.ayfaar.app.controllers.AuthController.UserPresentation;
import org.ayfaar.app.dao.UserDao;
import org.ayfaar.app.model.User;
import org.ayfaar.app.model.UserRoleEnum;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class UserServiceImpl implements UserService {

    UserDao userDao;

    @Inject
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    @Override
    public User createOrUpdate(UserPresentation userPresentation){
        User user = new User();
        user.setEmail(userPresentation.getEmail());
        user.setFirstname(userPresentation.getFirstname());
        user.setLastname(userPresentation.getLastname());
        user.setId(userPresentation.getId());
        user.setRole(UserRoleEnum.USER);
        return userDao.save(user);
    }
}
