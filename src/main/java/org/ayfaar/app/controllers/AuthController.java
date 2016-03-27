package org.ayfaar.app.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
public class AuthController {

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
    }

    @RequestMapping("current-user")
    public UserPresentation getCurrentUser() {
        return null;
    }

    private class UserPresentation {
        public String id;
        public String name;
        public String thumbnail;
        public String authProvider;
    }
}
