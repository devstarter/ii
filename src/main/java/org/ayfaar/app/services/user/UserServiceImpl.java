package org.ayfaar.app.services.user;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public Optional<UserPresentation> getPresentation(String email) {
        return Optional.of(new UserPresentation(email));
    }
}
