package org.ayfaar.app.services.user;


import org.ayfaar.app.dao.BasicCrudDao;
import org.ayfaar.app.dao.UserDao;
import org.ayfaar.app.model.CustomUser;
import org.ayfaar.app.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserService implements UserDetailsService {

    @Autowired
    BasicCrudDao<User> basicCrudDao;

    public CustomUser loadUserByUsername(String email) throws UsernameNotFoundException {

        CustomUser customUser = new CustomUser();

        User userByEmail = basicCrudDao.get("email", email);
        customUser.setFirstname(userByEmail.getEmail());
        customUser.setLastname(userByEmail.getLast_name());
        customUser.setUsername(userByEmail.getEmail());//Аутентификация по EMAIL!!!!!!!!!!!!
        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(userByEmail.getRole().toString());
        customUser.setAuthorities(authorityList);
        return customUser;
    }

}
