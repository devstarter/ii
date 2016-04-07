package org.ayfaar.app.dao;


import org.ayfaar.app.model.CustomUser;
import org.ayfaar.app.model.User;

public interface CustomUserDao{

    CustomUser loadUserByUsername(String username);
}
