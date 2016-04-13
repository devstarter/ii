package org.ayfaar.app.services.user;

import java.util.Optional;

public interface UserService {
    Optional<UserPresentation> getPresentation(String email);
}
