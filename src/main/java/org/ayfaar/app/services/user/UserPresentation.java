package org.ayfaar.app.services.user;

import org.ayfaar.app.model.User;
import org.ayfaar.app.services.moderation.UserRole;

public class UserPresentation {
    public Integer id;
    public String email;
    public String name;
    public UserRole role;

    public UserPresentation(User user) {
        id = user.getId();
        email = user.getEmail();
        email = user.getName();
        role = user.getRole();
    }
}
