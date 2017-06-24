package org.ayfaar.app.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class RobotsController {

    @Value("${site-base-url}")
    private String baseUrl;

    @RequestMapping("robots.txt")
    @ResponseBody
    public String getRobots(){
        return "Sitemap: " + baseUrl + "/sitemap.xml";
    }
}
