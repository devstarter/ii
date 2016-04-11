package org.ayfaar.app.utils;

import com.redfin.sitemapgenerator.WebSitemapGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@Service
@Slf4j
public class SiteMapGenerator {

    @Value("${OPENSHIFT_BASE_URL}")
    private String baseUrl;

    private ResourceLoader resourceLoader;

    private URLGenerator urlGenerator;

    @Inject
    public SiteMapGenerator(URLGenerator urlGenerator,ResourceLoader resourceLoader) {
        this.urlGenerator = urlGenerator;
        this.resourceLoader = resourceLoader;
    }

    private void generateSiteMap(List<String> urls) throws MalformedURLException {
        Resource resource = resourceLoader.getResource("classpath:static/");

        File myDir = null;
        try {
            myDir = resource.getFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        WebSitemapGenerator wsg = new WebSitemapGenerator(baseUrl, myDir);
        urls.parallelStream().forEach(s -> {
            try {
                wsg.addUrl(s);
            } catch (MalformedURLException e) {
                log.error("Malformed URL is occurred! ", e);
            }
        });
        wsg.write();
    }

    public void createSiteMap(){
        List<String> listURLs = urlGenerator.getURLs();
        try {
            generateSiteMap(listURLs);
        } catch (MalformedURLException e) {
            log.error("Malformed URL is occurred! ", e);
        }
    }
}
