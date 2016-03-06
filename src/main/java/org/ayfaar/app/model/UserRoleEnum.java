package org.ayfaar.app.model;

import org.springframework.security.core.GrantedAuthority;

public enum UserRoleEnum{

    ADMIN,
    USER,
    MODERATOR,
    ANONYMOUS;

    UserRoleEnum() {
    }


}