package org.ayfaar.app.controllers;

import org.ayfaar.app.utils.RegExpUtils;
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
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class Router {
    @Autowired ServletContext context;
    private final DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    @Value("${OPENSHIFT_HOMEDIR}")
    private String jbossDir;

    @RequestMapping("/")
    @ResponseBody
    public Object returnIndex(HttpServletRequest request) throws IOException {
        String index = "index.html";
        String path = request.getServletContext().getRealPath(index);
        if (path == null) {
            path = jbossDir+"app-deployments/current/repo/src/main/webapp/"+index;
        }
        return new FileSystemResource(path);
    }

    @RequestMapping("/new/**")
    @ResponseBody
    public Object returnNewIndex(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String index = "new/index.html";
        String path = request.getServletContext().getRealPath(index);

        Pattern pattern = Pattern.compile("/new/((t|p|i)/).*");
        Matcher matcher = pattern.matcher(request.getRequestURI());

        if(matcher.find()) {
            String newPath = request.getRequestURI().replace(matcher.group(1), "");
            response.sendRedirect(newPath);
        }

        if (path == null) {
            path = jbossDir+"app-deployments/current/repo/src/main/webapp/"+index;
        }
        return new FileSystemResource(path);
    }

    private String find(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory() && file.canRead()) {
                String result = find(file);
                if (!result.equals("not found")) {
                    return result;
                }
            } else if (file.getName().equals("google9ff4abadde5fb24d.html")) {
                return file.getAbsolutePath();
            }
        }
        return "not found";
    }

    /*@RequestMapping("/")
    public void redirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        redirectStrategy.setContextRelative(true);
        redirectStrategy.sendRedirect(request, response, "index.html");
    }*/
}
