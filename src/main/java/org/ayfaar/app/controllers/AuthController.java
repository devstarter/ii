package org.ayfaar.app.controllers;

import org.ayfaar.app.model.CustomUser;
import org.ayfaar.app.model.Role;
import org.ayfaar.app.model.User;
import org.ayfaar.app.services.user.CustomUserService;
import org.ayfaar.app.services.user.UserServiceImpl;
import org.ayfaar.app.utils.authentication.CustomAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    public void auth(@RequestParam Map<String,String> requestParams) throws IOException {
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
        UserPresentation userPresentation = new UserPresentation();
        userPresentation.email = requestParams.get("email");
        userPresentation.firstname = requestParams.get("first_name");
        userPresentation.id = Long.valueOf(requestParams.get("id"));
        userPresentation.lastname = requestParams.get("last_name");
        userPresentation.name = requestParams.get("name");
        userPresentation.thumbnail = requestParams.get("thumbnail");
        userPresentation.authProvider = requestParams.get("auth_provider");

        getCurrentUser(userPresentation);
        setAuthentication(userPresentation.email);//Аутентификация по EMAIL!!!!!!!!!!!!

    }

    private User getCurrentUser( UserPresentation userPresentation) {
        User user = new User();
        user.setEmail(userPresentation.email);
        user.setFirstname(userPresentation.firstname);
        user.setLastname(userPresentation.lastname);
        user.setId(userPresentation.id);
        user.setName(userPresentation.name);
        user.setThumbnail(userPresentation.thumbnail);
        user.setAuthProvider(userPresentation.authProvider);
        user.setRole("ROLE_USER");
        createOrUpdateUser(user);
        return user;
    }

    private class UserPresentation {
        private Long id;
        private String firstname;
        private String lastname;
        private String email;
        private String name;
        private String thumbnail;
        private String authProvider;
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
            if (principal instanceof CustomUser) return ((CustomUser) principal).getFirstName();
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

//            return new UsernamePasswordAuthenticationToken(auth.getName(),
//                    auth.getCredentials(), getAuthorities(auth.getName()));
            return new CustomAuthenticationToken(auth.getName(), getAuthorities(auth.getName()));

        }
    }
}
