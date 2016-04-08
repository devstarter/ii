package org.ayfaar.app.services.user;


import org.ayfaar.app.dao.UserDao;
import org.ayfaar.app.dao.impl.CustomUserDAOImpl;
import org.ayfaar.app.model.CustomUser;
import org.ayfaar.app.model.Role;
import org.ayfaar.app.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserService implements UserDetailsService {

    @Autowired
    UserDao userDao;

    public CustomUser loadUserByUsername(String email) throws UsernameNotFoundException {
        //return userDao.loadUserByUsername(username);
        CustomUser customUser = new CustomUser();

        User userByEmail = userDao.getUserByEmail(email);

        customUser.setFirstName(userByEmail.getEmail());
        customUser.setLastName(userByEmail.getLastname());
        customUser.setUsername(userByEmail.getEmail());//Аутентификация по EMAIL!!!!!!!!!!!!
        customUser.setPassword(""); //нужен только для LoginForm
        Role r = new Role();
        r.setName(userByEmail.getRole());
        List<Role> roles = new ArrayList<Role>();
        roles.add(r);
        customUser.setAuthorities(roles);
        return customUser;
    }

}
