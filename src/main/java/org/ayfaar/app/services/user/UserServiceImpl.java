package org.ayfaar.app.services.user;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.User;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class UserServiceImpl implements UserService {
    @Inject CommonDao commonDao;

    @Override
    public UserPresentation getPresentation(Integer id) {
        // todo implement logic
        return id == null ? null : new UserPresentation(commonDao.getOpt(User.class, id).get());
    }
}
