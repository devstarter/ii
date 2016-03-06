package org.ayfaar.app.dao;


import org.ayfaar.app.model.User;

public interface UserDao extends BasicCrudDao<User>{

    User getUserByEmail(String email);

    User save(User user);
}
