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
    public Object returnOldIndex(HttpServletRequest request) throws IOException {
        String index = "old/index.html";
        String path = request.getServletContext().getRealPath(index);
        if (path == null) {
            path = jbossDir+"app-deployments/current/repo/src/main/webapp/"+index;
        }
        return new FileSystemResource(path);
    }

    @RequestMapping("/**")
    @ResponseBody
    public Object returnIndex(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Redirect logic
        String url = request.getRequestURI();
        if (url.equals("/new") || url.equals("/main") || url.equals("/%D0%97%D0%B0%D0%B3%D0%BB%D0%B0%D0%B2%D0%BD%D0%B0%D1%8F_%D1%81%D1%82%D1%80%D0%B0%D0%BD%D0%B8%D1%86%D0%B0")) { // Заглавная_страница
            response.sendRedirect("/");
            return null;
        }
        String regexp = "/new/(([tpi]|item|term)/)?.*";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(request.getRequestURI());

        if(matcher.find()) {
            String newPath = url;
            newPath = newPath.replace("new/", "");
            if (matcher.group(1) != null) {
                newPath = newPath.replace(matcher.group(1), "");
            }
            response.sendRedirect(newPath);
            return null;
        }
        //

        String index = "index.html";
        String path = request.getServletContext().getRealPath(index);
        if (path == null) {
            path = jbossDir+"app-deployments/current/repo/src/main/webapp/"+index;
        }
        return new FileSystemResource(path);
    }
}
