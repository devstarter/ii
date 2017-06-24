package org.ayfaar.app.controllers;

import org.ayfaar.app.configs.SecurityConfig;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.User;
import org.ayfaar.app.services.moderation.Action;
import org.ayfaar.app.services.moderation.ModerationService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final CommonDao commonDao;
    private ModerationService moderationService;
    private final SecurityConfig.CustomAuthenticationProvider customAuthenticationProvider;

    @Inject
    public AuthController(SecurityConfig.CustomAuthenticationProvider customAuthenticationProvider, CommonDao commonDao, ModerationService moderationService) {
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.commonDao = commonDao;
        this.moderationService = moderationService;
    }

    @RequestMapping(method = RequestMethod.POST)
    /*
     Регистрируем нового пользователя и/или (если такой уже есть) назначаем его текущим для этой сессии

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
    public User registrate(@RequestParam String access_token,
                           @RequestParam String email,
                           @RequestParam String first_name,
                           @RequestParam String last_name,
                           @RequestParam String name,
                           @RequestParam String picture,
                           @RequestParam String thumbnail,
                           @RequestParam(required=false) String timezone,
                           @RequestParam Long id,
                           @RequestParam OAuthProvider auth_provider) throws IOException{
       User user = commonDao.getOpt(User.class, "email", email).orElse(
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
                    .build());

        boolean newUserFlag = user.getId() == null;

        if (!user.getAccessToken().equals(access_token)){
            user.setAccessToken(access_token);
        }
        user.setLastVisitAt(new Date());
        commonDao.save(user);

        Authentication request = new UsernamePasswordAuthenticationToken(user, null);
        Authentication authentication = customAuthenticationProvider.authenticate(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        if (newUserFlag) moderationService.notice(Action.NEW_USER, user.getName());
        return user;
    }

    @RequestMapping("login-as/{userId}")
    public void loginAs(@PathVariable Integer userId, HttpServletResponse response) throws IOException{
        User user = commonDao.getOpt(User.class, userId).get();

        Authentication request = new UsernamePasswordAuthenticationToken(user, null);
        Authentication authentication = customAuthenticationProvider.authenticate(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        response.sendRedirect("/%D1%8F");
    }
}
