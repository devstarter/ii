package org.ayfaar.app.utils;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GoogleService {
    @Value("${google.api.key}") private String API_KEY;

    @Inject RestTemplate restTemplate;

    public VideoInfo getVideoInfo(String id) {
        final Map response = restTemplate.getForObject("https://content.googleapis.com/youtube/v3/videos?part={part}&id={id}&key={key}",
                Map.class, "snippet", id, API_KEY);
        //noinspection unchecked
        final List<Map> items = (List<Map>) response.get("items");
        if (items.isEmpty()) throw new RuntimeException("Video private or removed");
        final Map snippet = (Map) items.get(0).get("snippet");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            return new VideoInfo((String) snippet.get("title"), dateFormat.parse((String) snippet.get("publishedAt")));
        } catch (ParseException e) {
            throw new RuntimeException("Video date parsing error", e);
        }
    }

    public static String extractVideoIdFromYoutubeUrl(String url) {
        //https://www.youtube.com/watch?v=044VwC_uptU
        Matcher matcher = Pattern.compile("^https?://www\\.youtube\\.com/watch\\?v=([^&]+)").matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            // https://youtu.be/1I1cy6z-FgY
            matcher = Pattern.compile("^https?://youtu.be/(.*)$").matcher(url);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        throw new RuntimeException("Cannot resolve video id");
    }

    public static String extractDocIdFromUrl(String url) {
        //https://docs.google.com/document/d/1iWY8qI5Qn1V_90VpzfhFDTyAcNgati9u6sTv-A-gWQg/edit?usp=sharing
        //https://drive.google.com/file/d/0BwGttgSD-WcTbTJFbWplN1hwcFU/view?usp=sharing
        Matcher matcher = Pattern.compile("^https?://(docs|drive)\\.google\\.com/(document|file)/d/([^/]+)").matcher(url);
        if (matcher.find()) {
            return matcher.group(3);
        }
        throw new RuntimeException("Cannot resolve document id");
    }

    public DocInfo getDocInfo(String id) {
        final DocInfo doc = restTemplate.getForObject("https://www.googleapis.com/drive/v2/files/{id}?key={key}", DocInfo.class, id, API_KEY);
        Assert.notNull(doc.title);
        return doc;
    }

    public static class VideoInfo {
        public Date publishedAt;
        public String title;

        public VideoInfo(String title, Date publishedAt) {
            this.title = title;
            this.publishedAt = publishedAt;
        }
    }

    @NoArgsConstructor
    public static class DocInfo {
        public String title;
        public String iconLink;
        public String thumbnailLink;
        public String mimeType;
        public String downloadUrl;
        public String fileExtension;
        public Integer fileSize;
    }
}
