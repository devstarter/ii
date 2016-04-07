package org.ayfaar.app.controllers;

import org.ayfaar.app.model.CustomUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {
    @RequestMapping(value="/login", method = RequestMethod.GET)
    public String securedHome(ModelMap model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomUser user=null;
        if (principal instanceof CustomUser) {
            user = ((CustomUser)principal);
        }

        String name = user.getUsername();
        model.addAttribute("username", name);
        return "index";
    }
}
