
package org.ayfaar.app.services.user;

import org.ayfaar.app.dao.UserDao;
import org.ayfaar.app.model.User;
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
    public User createOrUpdate(User user){
        return userDao.save(user);
    }
}