package org.ayfaar.app.utils;

import org.ayfaar.app.model.User;
import org.ayfaar.app.services.moderation.UserRole;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.inject.Provider;
import java.util.Optional;

@Component
public class CurrentUserProvider implements Provider<Optional<User>> {
    public Optional<User> get(){
        if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null) return Optional.empty();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal instanceof User ? Optional.of((User) principal) : Optional.empty();
    }

    public UserRole getCurrentAccessLevel() {
//        return UserRole.ROLE_ADMIN;
        return get().isPresent() ? get().get().getRole() : UserRole.ROLE_ANONYMOUS;
    }
    /*@Override
    public Optional<User> get() {
        return AuthController.get();
    }*/
}
