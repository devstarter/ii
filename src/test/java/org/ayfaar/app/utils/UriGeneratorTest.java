package org.ayfaar.app.utils;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.controllers.SiteMapController;
import org.junit.Test;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class UriGeneratorTest extends IntegrationTest {
    @Inject
    TermServiceImpl termService;
    @Inject
    URLGeneratorImpl urlGenerator;
    @Inject
    SiteMapController siteMapController;

    @Test
    public void test(){

        List<String> list = new ArrayList<>();

        list = urlGenerator.generateTermsUrls();

//        for(String s : list){
//            System.out.println(s);
//        }

        siteMapController.createSiteMap();
    }
}
