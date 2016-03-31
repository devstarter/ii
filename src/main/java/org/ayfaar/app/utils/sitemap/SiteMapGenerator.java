package org.ayfaar.app.utils.sitemap;

import com.redfin.sitemapgenerator.WebSitemapGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.stream.Stream;

@Service
@Slf4j
@EnableScheduling
@Controller
@RequestMapping("api")
public class SiteMapGenerator {

    @Value("${site-base-url}")
    private String baseUrl;

    @Value("${sitemap-dir}")
    private String sitemapDir;

    private ResourceLoader resourceLoader;
    private URLGenerator urlGenerator;

    @Inject
    public SiteMapGenerator(URLGenerator urlGenerator,ResourceLoader resourceLoader) {
        this.urlGenerator = urlGenerator;
        this.resourceLoader = resourceLoader;
    }

    private void generateSiteMap(Stream<String> urls) throws MalformedURLException {
        Resource resource = resourceLoader.getResource(sitemapDir);

        File sitemapDirObj = null;
        try {
            sitemapDirObj = resource.getFile();
        } catch (IOException e) {
            log.error("Error locating sitemap dir `{}`", sitemapDir, e);
        }

        WebSitemapGenerator wsg = new WebSitemapGenerator(baseUrl, sitemapDirObj);
        urls.forEach(s -> {
            try {
                wsg.addUrl(s);
            } catch (MalformedURLException e) {
                log.error("Url resolving error", e);
            }
        });
        wsg.write();
        log.info("Sitemap location: {}", sitemapDirObj.getAbsolutePath() + "/sitemap.xml");
    }

    @Scheduled(cron="0 0 0 * * *") // every day at 0 hours
    @RequestMapping("update-sitemap")
    public void createSiteMap(){
        log.info("Sitemap generation started");
        Stream<String> streamURLs = urlGenerator.getURLs();
        try {
            generateSiteMap(streamURLs);
        } catch (MalformedURLException e) {
            log.error("Url resolving error", e);
        }
        log.info("Sitemap generation finished");
    }
}
