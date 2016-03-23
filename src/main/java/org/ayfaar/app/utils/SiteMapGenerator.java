package org.ayfaar.app.utils;

import com.redfin.sitemapgenerator.WebSitemapGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

@Service
public class SiteMapGenerator {

    @Value("${OPENSHIFT_DATA_DIR}")
    private String dir;

    public void generateSiteMap(List<String> urls) throws MalformedURLException {
        File myDir = new File(dir);
        WebSitemapGenerator wsg = new WebSitemapGenerator("http://ii.ayfaar.org/", myDir);
        for (String url : urls) {
            wsg.addUrl(url);
        }
        wsg.write();
    }
}
