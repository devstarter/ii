package org.ayfaar.app.services.user;

import org.ayfaar.app.dao.UserDao;
import org.ayfaar.app.model.CurrentUser;
import org.ayfaar.app.model.User;
import org.ayfaar.app.model.UserRoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service("currentUserDetailsService")
public class CurrentUserDetailsService implements UserDetailsService{
    private final UserDao userDao;

    @Autowired
    public CurrentUserDetailsService(UserDao userDao) {
        this.userDao = userDao;
    }
    @Override
    public CurrentUser loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userDao.getUserByEmail(email);
        if ( user == null ) {
            throw new UsernameNotFoundException("User with name " + email + " not found");
        }
        return new CurrentUser(user);
    }
}
