package org.ayfaar.app.services.user;

import org.ayfaar.app.controllers.AuthController;
import org.ayfaar.app.model.User;

public interface UserService {
    User getUserByEmail(String email);

    User createOrUpdate(User user);
}