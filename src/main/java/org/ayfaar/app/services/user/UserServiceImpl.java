package org.ayfaar.app.services.user;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public UserPresentation getPresentation(Integer id) {
        return new UserPresentation();
    }
}
