package org.ayfaar.app.utils;

import lombok.Builder;
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
        return VideoInfo.builder()
                .title((String) snippet.get("title"))
                .publishedAt(dateFormat.parse((String) snippet.get("publishedAt")))
                .build();
    }

    @Builder
    public static class VideoInfo {
        public Date publishedAt;
        public String title;
    }
}
