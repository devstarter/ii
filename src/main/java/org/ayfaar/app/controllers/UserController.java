package org.ayfaar.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
public class UserController {

    //private final DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private String email;

    @RequestMapping(value = "/new/api/login",method = RequestMethod.POST)
    public void catchUser(@RequestParam String email, HttpServletResponse response, HttpServletRequest request)  {

        System.out.println("i'm Users email and i'm here!! " + email);

    }
}
