package org.ayfaar.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

@Controller
public class Router {
    @Autowired ServletContext context;
//    private final DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @RequestMapping("/")
    @ResponseBody
    public FileSystemResource returnIndex(HttpServletRequest request) {
        return new FileSystemResource(request.getRealPath("index.html"));
    }

    /*@RequestMapping("/")
    public void redirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        redirectStrategy.setContextRelative(true);
        redirectStrategy.sendRedirect(request, response, "index.html");
    }*/
}
