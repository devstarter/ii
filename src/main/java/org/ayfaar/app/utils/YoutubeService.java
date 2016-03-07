package org.ayfaar.app.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class YoutubeService {
    @Value("${youtube.api.key}") private String KEY;

    @Inject RestTemplate restTemplate;

    public VideoInfo getInfo(String id) throws ParseException {
        final Map response = restTemplate.getForObject("https://content.googleapis.com/youtube/v3/videos?part={part}&id={id}&key={key}",
                Map.class, "snippet", id, KEY);
        //noinspection unchecked
        final Map snippet = (Map) ((List<Map>) response.get("items")).get(0).get("snippet");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return new VideoInfo((String) snippet.get("title"), dateFormat.parse((String) snippet.get("publishedAt")));
    }

    public static class VideoInfo {
        public Date publishedAt;
        public String title;

        public VideoInfo(String title, Date publishedAt) {
            this.title = title;
            this.publishedAt = publishedAt;
        }
    }
}
