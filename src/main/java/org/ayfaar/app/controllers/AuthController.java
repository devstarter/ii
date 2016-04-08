package org.ayfaar.app.controllers;

import org.ayfaar.app.model.CustomUser;
import org.ayfaar.app.model.Role;
import org.ayfaar.app.model.User;
import org.ayfaar.app.services.user.CustomUserService;
import org.ayfaar.app.services.user.UserServiceImpl;
import org.ayfaar.app.utils.authentication.CustomAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;
import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Inject
    UserServiceImpl userService;
    @Inject
    CustomUserService customUserService;

    @RequestMapping(method = RequestMethod.POST)
    /**
     * Регистрируем нового пользователя и/или (если такой уже есть) назначаем его текущим для этой сессии
     */
    public void auth(UserPresentation userPresentation) throws IOException{//@RequestParam Map<String,String> requestParams) throws IOException {
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

        userPresentation.getEmail();
        userPresentation.getFirst_name();
        userPresentation.getId();
        userPresentation.getLast_name();
        userPresentation.getThumbnail();
        userPresentation.getName();
        userPresentation.getAuth_provider();

        getCurrentUser(userPresentation);
        setAuthentication(userPresentation.email);//Аутентификация по EMAIL!!!!!!!!!!!!

    }

    private User getCurrentUser( UserPresentation userPresentation) {
        User user = new User();
        user.setEmail(userPresentation.email);
        user.setFirstname(userPresentation.first_name);
        user.setLastname(userPresentation.last_name);
        user.setId(userPresentation.id);
        user.setName(userPresentation.name);
        user.setThumbnail(userPresentation.thumbnail);
        user.setAuthprovider(userPresentation.auth_provider);
        user.setRole("ROLE_USER");
        createOrUpdateUser(user);
        return user;
    }
    @Getter @Setter
    public static class UserPresentation {
        private Long id;
        private String first_name;
        private String last_name;
        private String email;
        private String name;
        private String thumbnail;
        private String auth_provider;

        public UserPresentation() {
        }
    }


    private void createOrUpdateUser(User user){//Обновляем или сохраняем в базу
        userService.createOrUpdate(user);
    }

    @RequestMapping("principal")
    public String getCurrentUser(ModelMap model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null){
            return "Current user is not authentificated";
        }else {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUser) return ((CustomUser) principal).getFirstname();
            else return principal.toString();
        }

    }


    //Аутентификация
    private List<Role> getAuthorities(String name) {
        List<Role> authorities = customUserService.loadUserByUsername(name).getAuthorities();
        return authorities;
    }

    private Authentication setAuthentication(String name) {
        AuthenticationManager authenticationManager;
        authenticationManager = new SampleAuthenticationManager();
        //Authentication request = new UsernamePasswordAuthenticationToken(name, null, getAuthorities(name));
        Authentication request = new CustomAuthenticationToken(name, getAuthorities(name));
        Authentication result = authenticationManager.authenticate(request);
        SecurityContextHolder.getContext().setAuthentication(result);
        return SecurityContextHolder.getContext().getAuthentication();
    }

    class SampleAuthenticationManager implements AuthenticationManager {

        public Authentication authenticate(Authentication auth) throws AuthenticationException {

            return new CustomAuthenticationToken(auth.getName(), getAuthorities(auth.getName()));
        }
    }
}
