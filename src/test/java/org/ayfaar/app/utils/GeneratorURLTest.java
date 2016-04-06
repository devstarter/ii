package org.ayfaar.app.utils;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.controllers.AuthController;
import org.ayfaar.app.model.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;

import javax.inject.Inject;
import java.util.List;


public class GeneratorURLTest extends IntegrationTest{
    @Inject SiteMapGenerator siteMapGenerator;
    @Inject URLGenerator urlGenerator;
    @Inject
    ResourceLoader resourceLoader;

    @Inject
    AuthController authController;
    @Value("${OPENSHIFT_SITEMAP_DIR}")
    private String dir;
    @Inject
    User user;
    @Test
    public void test(){


//        for (String s : urlGenerator.getCategoriesURL()) {
//            System.out.println(s);
//        }
//        Resource resource = resourceLoader.getResource("file:sitemap/");
//        siteMapGenerator.createSiteMap();

//        try {
//            System.out.println(resource.getFile().exists());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        user.setEmail("userTest@qq.qq");
    }

}
