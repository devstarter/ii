package org.ayfaar.app.controllers.search;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class RobotsController {

    @Value("${OPENSHIFT_BASE_URL}")
    private String baseUrl;

    @RequestMapping("robots.txt")
    public String getRobots(){
        return "Sitemap: " + baseUrl + "sitemap.xml";
    }
}
