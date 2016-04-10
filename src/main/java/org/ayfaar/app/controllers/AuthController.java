package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.BasicCrudDao;
import org.ayfaar.app.dao.UserDao;
import org.ayfaar.app.model.User;
import org.ayfaar.app.services.moderation.AccessLevel;
import org.ayfaar.app.services.user.CustomUserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Inject
    UserDao userDao;
    @Inject
    BasicCrudDao<User> basicCrudDao;
    @Inject
    CustomUserService customUserService;

    @RequestMapping(method = RequestMethod.POST)
    /**
     * Регистрируем нового пользователя и/или (если такой уже есть) назначаем его текущим для этой сессии
     */
    public void auth(User user) throws IOException{//@RequestParam Map<String,String> requestParams) throws IOException {
        /**
         Пример входных данных (requestParams):

         access_token:CAANCEx9hQ8ABACe5zBAPE1fThMsaJDHQ0oolOvZCsiOAoFgbj65BiZC5qFG557wYl71CRLZBBipi1JeZCZABkeD7PuurKplra04wvaGSiNnHdnWQZAqZBt1sLtps38DDOJ0RAUNlSDKnMjAkt7bZClUtxLCCF1lQk4NLIXMtuxXiKkLCnojk7KtoQbZBRbPTqzdadfbifnGUrOAZDZD
         email:sllouyssgort@gmail.com
         first_name:Sllouyssgort
         id:1059404344124694
         last_name:Smaay-Grriyss
         name:Sllouyssgort Smaay-Grriyss
         picture:https://graph.facebook.com/1059404344124694/picture
         thumbnail:https://graph.facebook.com/1059404344124694/picture
         timezone:3
         verified:true
         auth_provider:vk
         */
        createOrUpdateUser(user);
        setAuthentication(user);
    }

    private void createOrUpdateUser(User user){//Обновляем или сохраняем в базу
        user.setRole(AccessLevel.ROLE_EDITOR);
        basicCrudDao.save(user);
    }

    @RequestMapping("current")
    public User getCurrentUser(@AuthenticationPrincipal User currentUser){
        return currentUser;
    }

    //Аутентификация
    private List<GrantedAuthority> getAuthorities(String email) {
        List<GrantedAuthority> authorities = customUserService.loadUserByUsername(email).getAuthorities();
        return authorities;
    }

    private Authentication setAuthentication(User user) {
        AuthenticationManager authenticationManager;
        authenticationManager = new SampleAuthenticationManager();
        Authentication request = new UsernamePasswordAuthenticationToken(user, null, getAuthorities(user.getEmail()));
        Authentication result = authenticationManager.authenticate(request);
        SecurityContextHolder.getContext().setAuthentication(result);
        return SecurityContextHolder.getContext().getAuthentication();
    }

    class SampleAuthenticationManager implements AuthenticationManager {

        public Authentication authenticate(Authentication auth) throws AuthenticationException {

            return new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), auth.getAuthorities());
        }
    }
}
