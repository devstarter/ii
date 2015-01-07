package org.ayfaar.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class Router {
    @Autowired ServletContext context;
    private final DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    @Value("${OPENSHIFT_HOMEDIR}")
    private String jbossDir;

    @RequestMapping("/old")
    @ResponseBody
    public Object returnIndex(HttpServletRequest request) throws IOException {
        String index = "old/index.html";
        String path = request.getServletContext().getRealPath(index);
        if (path == null) {
            path = jbossDir+"app-deployments/current/repo/src/main/webapp/"+index;
        }
        return new FileSystemResource(path);
    }

    @RequestMapping("/**")
    @ResponseBody
    public Object returnNewIndex(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String index = "index.html";
        String path = request.getServletContext().getRealPath(index);

        String regexp = "/new/(([tpi]|item|term)/)?.*";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(request.getRequestURI());

        if(matcher.find()) {
            String newPath = request.getRequestURI();
            newPath = newPath.replace("new/", "");
            if (matcher.group(1) != null) {
                newPath = newPath.replace(matcher.group(1), "");
            }
            response.sendRedirect(newPath);
        }

        if (path == null) {
            path = jbossDir+"app-deployments/current/repo/src/main/webapp/"+index;
        }
        return new FileSystemResource(path);
    }
}
