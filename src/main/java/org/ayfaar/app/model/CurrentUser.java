package org.ayfaar.app.model;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;
import java.util.List;

public class CurrentUser extends org.springframework.security.core.userdetails.User {

    private User user;
    private List<UserRoleEnum> authorities;

    public CurrentUser(User user) {
        super(user.getEmail(), user.getFirstname(), AuthorityUtils.createAuthorityList(user.getRole().toString()));
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return user.getId();
    }

    public UserRoleEnum getRole() {
        return user.getRole();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAuthorities(List<UserRoleEnum> authorities) {
        this.authorities = authorities;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return super.getAuthorities();
    }
}
