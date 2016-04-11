package org.ayfaar.app.utils;

import com.redfin.sitemapgenerator.WebSitemapGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

@Service
public class SiteMapGenerator {

    private static final Logger logger = LoggerFactory.getLogger(SiteMapGenerator.class);

    @Value("${OPENSHIFT_DATA_DIR}")
    private String dir;

    @Value("${OPENSHIFT_BASE_URL}")
    private String baseUrl;

    @Inject
    URLGenerator urlGenerator;

    private void generateSiteMap(List<String> urls) throws MalformedURLException {
        File myDir = new File(dir);
        WebSitemapGenerator wsg = new WebSitemapGenerator(baseUrl, myDir);
        urls.stream().forEach(s -> {
            try {
                wsg.addUrl(s);
            } catch (MalformedURLException e) {
                logger.error("Exception", e);
            }
        });
        wsg.write();
    }

    public void createSiteMap(){
        List<String> listURLs = urlGenerator.getURLs();
        try {
            generateSiteMap(listURLs);
        } catch (MalformedURLException e) {
            logger.error("Exception", e);
        }
    }
}
