package org.ayfaar.app.controllers;

import org.ayfaar.app.configs.SecurityConfig;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.io.IOException;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Inject CommonDao commonDao;
    @Inject SecurityConfig.CustomAuthenticationProvider customAuthenticationProvider;

    @RequestMapping(method = RequestMethod.POST)
    /**
     * Регистрируем нового пользователя и/или (если такой уже есть) назначаем его текущим для этой сессии
     *
     Пример входных данных:
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
    public void auth(@RequestParam String access_token,
                     @RequestParam String email,
                     @RequestParam String first_name,
                     @RequestParam String last_name,
                     @RequestParam String name,
                     @RequestParam String picture,
                     @RequestParam String thumbnail,
                     @RequestParam String timezone,
                     @RequestParam Long id,
                     @RequestParam OAuthProvider auth_provider) throws IOException{
        User user = commonDao.getOpt(User.class, email).orElseGet(() -> commonDao.save(
                User.builder()
                    .accessToken(access_token)
                    .email(email)
                    .oauthProvider(auth_provider)
                    .firstName(first_name)
                    .lastName(last_name)
                    .name(name)
                    .thumbnail(thumbnail)
                    .picture(picture)
                    .timezone(timezone)
                    .providerId(id)
                    .build()));

        Authentication request = new UsernamePasswordAuthenticationToken(user, null);
        Authentication authentication = customAuthenticationProvider.authenticate(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static User getCurrentUser(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal instanceof User ? (User) principal : null;
    }

    @RequestMapping("current")
    public User getCurrentUser(@AuthenticationPrincipal User currentUser){
        return currentUser;
    }
}
