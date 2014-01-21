package org.ayfaar.app.controllers;

import org.springframework.core.io.FileSystemResource;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class Router {
//    @Autowired ServletContext context;
    private final DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
//    @Value("${OPENSHIFT_JBOSSEWS_DIR}")
//    private String jbossDir;

    @RequestMapping("/")
    @ResponseBody
    public Object returnIndex(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getServletContext().getRealPath("index.html");
        if (path != null) {
            return new FileSystemResource(path);
        } else {
            redirectStrategy.setContextRelative(true);
            redirectStrategy.sendRedirect(request, response, "index.html");
            return null;
        }
    }

    /*@RequestMapping("/")
    public void redirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        redirectStrategy.setContextRelative(true);
        redirectStrategy.sendRedirect(request, response, "index.html");
    }*/
}
