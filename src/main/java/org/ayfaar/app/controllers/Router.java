package org.ayfaar.app.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class Router {
//    @Autowired ServletContext context;
//    private final DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    @Value("${OPENSHIFT_JBOSSEWS_DIR}")
    private String jbossDir;

    @RequestMapping("/")
    @ResponseBody
    public FileSystemResource returnIndex(HttpServletRequest request) {
        String path = request.getServletContext().getRealPath("index.html");
        if (path == null) {
            path = jbossDir + "webapps/index.html";
        }
        return new FileSystemResource(path);
    }

    /*@RequestMapping("/")
    public void redirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        redirectStrategy.setContextRelative(true);
        redirectStrategy.sendRedirect(request, response, "index.html");
    }*/
}
