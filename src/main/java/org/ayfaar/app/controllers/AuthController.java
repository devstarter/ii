package org.ayfaar.app.controllers;

import org.ayfaar.app.model.CustomUser;
import org.ayfaar.app.model.Role;
import org.ayfaar.app.services.user.CustomUserService;
import org.ayfaar.app.services.user.UserServiceImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    UserPresentation userPresentation = new UserPresentation();
    @RequestMapping(method = RequestMethod.POST)
    /**
     * Регистрируем нового пользователя и/или (если такой уже есть) назначаем его текущим для этой сессии
     */
    //@PreAuthorize("hasRole('USER')")
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

        userPresentation.email = requestParams.get("email");
        userPresentation.firstname = requestParams.get("first_name");
        userPresentation.id = Long.valueOf(requestParams.get("id"));
        userPresentation.lastname = requestParams.get("last_name");

        createOrUpdateUser(); //Обновляем или сохраняем в базу
        setAuthentication(userPresentation.firstname, "");//Аутентификация

    }

    @RequestMapping("current-user")
    public UserPresentation getCurrentUser() {
        return null;
    }

    public class UserPresentation {
        public Long id;
        public String firstname;
        public String lastname;
        public String email;
    }

    @Inject
    UserServiceImpl userService;

    public void createOrUpdateUser(){
        userService.createOrUpdate(userPresentation);
    }

    @RequestMapping("principal")
    public String getCurrentUser(ModelMap model){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUser) {
            return ((CustomUser)principal).getFirstName();
        }else return principal.toString();

    }

    @RequestMapping("current")
    public String getCurrentUserName(@AuthenticationPrincipal CustomUser customUser){
        return customUser.getUsername();
    }

    //Аутентификация
    private AuthenticationManager authenticationManager;
    @Inject
    CustomUserService customUserService;

    public List<Role> getAuthorities() {
        List<Role> authorities = customUserService.loadUserByUsername(userPresentation.firstname).getAuthorities();
        return authorities;
    }


    public Authentication setAuthentication(String name, String password) {

        authenticationManager = new SampleAuthenticationManager();
        Authentication request = new UsernamePasswordAuthenticationToken(name, password, getAuthorities());
        Authentication result = authenticationManager.authenticate(request);
        SecurityContextHolder.getContext().setAuthentication(result);
        return SecurityContextHolder.getContext().getAuthentication();
    }

    class SampleAuthenticationManager implements AuthenticationManager {

        public Authentication authenticate(Authentication auth) throws AuthenticationException {

            return new UsernamePasswordAuthenticationToken(auth.getName(),
                    auth.getCredentials(), getAuthorities());

        }
    }
}
