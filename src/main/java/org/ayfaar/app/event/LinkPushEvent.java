package org.ayfaar.app.event;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class LinkPushEvent extends PushEvent implements HasUrl {
    protected String url;

    public LinkPushEvent(String title, String url) {
        this.title = title;
        this.url = getUrlToTerm(url);
    }

    public LinkPushEvent() {
    }

    @Override
    public String getUrl() {
        return url;
    }

    protected static String getUrlToTerm(String term) {
        try {
            return BASE_URL+"/"+ URLEncoder.encode(term, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
