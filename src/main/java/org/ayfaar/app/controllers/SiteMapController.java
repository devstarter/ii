package org.ayfaar.app.controllers;

import org.ayfaar.app.utils.SiteMapGenerator;
import org.ayfaar.app.utils.URLGeneratorImpl;
import org.springframework.stereotype.Controller;
import javax.inject.Inject;
import java.net.MalformedURLException;
import java.util.List;

@Controller
public class SiteMapController {

    @Inject
    URLGeneratorImpl urlGenerator;

    @Inject
    SiteMapGenerator siteMapGenerator;

    public void createSiteMap(){

        List<String> listURLs = urlGenerator.generateTermsUrls();

        try {
            siteMapGenerator.generateSiteMap(listURLs);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
