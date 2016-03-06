package org.ayfaar.app.utils;

import com.redfin.sitemapgenerator.WebSitemapGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.stream.Stream;

@Service
@Slf4j
@EnableScheduling
public class SiteMapGenerator {

    @Value("${OPENSHIFT_BASE_URL}")
    private String baseUrl;

    @Value("${OPENSHIFT_SITEMAP_DIR}")
    private String sitemapResource;

    private ResourceLoader resourceLoader;
    private URLGenerator urlGenerator;

    @Inject
    public SiteMapGenerator(URLGenerator urlGenerator,ResourceLoader resourceLoader) {
        this.urlGenerator = urlGenerator;
        this.resourceLoader = resourceLoader;
    }

    private void generateSiteMap(Stream<String> urls) throws MalformedURLException {
        Resource resource = resourceLoader.getResource("file:" + sitemapResource);

        File sitemapDir = null;
        try {
            sitemapDir = resource.getFile();
        } catch (IOException e) {
            log.error("Path is not accessible. ", e);
        }

        WebSitemapGenerator wsg = new WebSitemapGenerator(baseUrl, sitemapDir);
        urls.forEach(s -> {
            try {
                wsg.addUrl(s);
            } catch (MalformedURLException e) {
                log.error("Malformed URL is occurred! ", e);
            }
        });
        wsg.write();
    }

    public void createSiteMap(){
        Stream<String> streamURLs = urlGenerator.getURLs();
        try {
            generateSiteMap(streamURLs);
        } catch (MalformedURLException e) {
            log.error("Malformed URL is occurred! ", e);
        }
    }

    @Scheduled(cron="0 0 12 * * ?" )//Fire at 12pm (noon) every day
    public void update(){
        createSiteMap();
    }
}
