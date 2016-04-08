package org.ayfaar.app.dao.impl;

import org.ayfaar.app.dao.UserDao;
import org.ayfaar.app.model.CustomUser;
import org.ayfaar.app.model.Role;
import org.ayfaar.app.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


public class CustomUserDAOImpl{

    UserDao userDao;
    public CustomUser loadUserByUsername(final String username) {
//        CustomUser customUser = new CustomUser();
//
//        User userByEmail = userDao.getUserByEmail(username);
//
//        customUser.setFirstName(userByEmail.getFirstname());
//        customUser.setLastName(userByEmail.getLastname());
//        customUser.setUsername(userByEmail.getFirstname());
//        customUser.setPassword("");
//        Role r = new Role();
//        r.setName(userByEmail.getRole());
//        List<Role> roles = new ArrayList<Role>();
//        roles.add(r);
//        customUser.setAuthorities(roles);
        return null;

    }


}