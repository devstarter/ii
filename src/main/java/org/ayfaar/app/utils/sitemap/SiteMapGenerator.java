package org.ayfaar.app.utils.sitemap;

import com.redfin.sitemapgenerator.WebSitemapGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
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
@Profile("default")
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

    private void generateSiteMap(Stream<String> urls) throws IOException {
        Resource resource = resourceLoader.getResource("file:" + sitemapDir);
        if (!resource.exists()) {
            throw new RuntimeException("Error locating sitemap dir "+sitemapDir);
        }
        File sitemapDirObj = resource.getFile();

        WebSitemapGenerator wsg = new WebSitemapGenerator(baseUrl, sitemapDirObj);
        urls.forEach(s -> {
            try {
                wsg.addUrl(s);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        });
        wsg.write();
        log.info("Sitemap location: {}", sitemapDirObj.getAbsolutePath() + "/sitemap.xml");
    }

    @Scheduled(cron="0 0 0 * * *") // every day at 0 hours
    @RequestMapping("update-sitemap")
    public void createSiteMap() throws IOException {
        log.info("Sitemap generation started");
        generateSiteMap(urlGenerator.getURLs());
        log.info("Sitemap generation finished");
    }
}
