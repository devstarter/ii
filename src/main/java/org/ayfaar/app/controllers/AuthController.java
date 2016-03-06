package org.ayfaar.app.controllers;

import org.ayfaar.app.model.CurrentUser;
import org.ayfaar.app.model.User;

import org.ayfaar.app.model.UserRoleEnum;
import org.ayfaar.app.services.user.CurrentUserDetailsService;
import org.ayfaar.app.services.user.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
public class AuthController{
    UserPresentation userPresentation = new UserPresentation();

    CurrentUserDetailsService currentUserDetailsService;

    @Inject
    public AuthController(CurrentUserDetailsService currentUserDetailsService) {
        this.currentUserDetailsService = currentUserDetailsService;

    }

    String name;
    ResponseEntity responseEntity;
    List<String> list = new ArrayList<>();
    @RequestMapping(method = RequestMethod.POST)//consumes = {"application/json"}
    /**
     * Регистрируем нового пользователя и/или (если такой уже есть) назначаем его текущим для этой сессии
     */

    public void auth(@RequestParam Map<String,String> requestParams) throws IOException { //@RequestParam Map<String,String> requestParams
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
//        userPresentation.getEmail();
//        userPresentation.getFirstname();
//        userPresentation.getId();
//        userPresentation.getLastname();

        //userPresentation.getThumbnail();
        //userPresentation.getTimezone();
        //userPresentation.getVerified();
        //userPresentation.getAuthProvider();

        userPresentation.email = requestParams.get("email");
        userPresentation.firstname = requestParams.get("first_name");
        userPresentation.id = Long.valueOf(requestParams.get("id"));
        userPresentation.lastname = requestParams.get("last_name");
//
//        userPresentation.thumbnail = requestParams.get("thumbnail");
//        userPresentation.timezone = requestParams.get("timezone");
//        userPresentation.verified = requestParams.get("verified");
//        userPresentation.authProvider = requestParams.get("auth_provider");

        createOrUpdateUser(); //Обновляем или сохраняем в базу
        setAuthentication(userPresentation.email, userPresentation.firstname);//Аутентификация

    }

    @RequestMapping(value = "/current-user")//Сейчас возвращается РОЛЬ
    public Collection<GrantedAuthority> getCurrentUser() {
        CurrentUser currentUser = currentUserDetailsService.loadUserByUsername(userPresentation.email);
        Collection<GrantedAuthority> authorities = currentUser.getAuthorities();
        return authorities;
    }

    public class UserPresentation {
        private Long id;
        private String firstname;
        private String lastname;
        private String email;
//        public String thumbnail;
//        public String timezone;
//        public String verified;
//        public String authProvider;

        UserPresentation() {
        }


        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }
//
        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }
//
        public String getEmail() {
            return email;
        }
//
        public void setEmail(String email) {
            this.email = email;
        }
//
//        public String getThumbnail() {
//            return thumbnail;
//        }
//
//        public void setThumbnail(String thumbnail) {
//            this.thumbnail = thumbnail;
//        }
//
//        public String getTimezone() {
//            return timezone;
//        }
//
//        public void setTimezone(String timezone) {
//            this.timezone = timezone;
//        }
//
//        public String getVerified() {
//            return verified;
//        }
//
//        public void setVerified(String verified) {
//            this.verified = verified;
//        }
//
//        public String getAuthProvider() {
//            return authProvider;
//        }
//
//        public void setAuthProvider(String authProvider) {
//            this.authProvider = authProvider;
//        }
    }

    @Inject
    UserServiceImpl userService;

    public void createOrUpdateUser(){
        userService.createOrUpdate(userPresentation);
    }

    @RequestMapping(value = "/auth_user")//для проверки доступен ли principal
    public String authenticatedUser(){
        String username;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        if(SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            return username + " is Authenticated";
        }else return username + " is not Authenticated";

    }

    //Аутентификация
    private AuthenticationManager authenticationManager;

    public Authentication setAuthentication(String name, String password) {

        authenticationManager = new SampleAuthenticationManager();
        Authentication request = new UsernamePasswordAuthenticationToken(name, password, getCurrentUser());
        Authentication result = authenticationManager.authenticate(request);
        SecurityContextHolder.getContext().setAuthentication(result);
        return SecurityContextHolder.getContext().getAuthentication();
    }

    class SampleAuthenticationManager implements AuthenticationManager {

        public Authentication authenticate(Authentication auth) throws AuthenticationException {

                return new UsernamePasswordAuthenticationToken(auth.getName(),
                        auth.getCredentials(), getCurrentUser());

        }
    }
}
