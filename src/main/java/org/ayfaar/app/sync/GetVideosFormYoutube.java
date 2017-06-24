package org.ayfaar.app.sync;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.event.EventPublisher;
import org.ayfaar.app.event.SysLogEvent;
import org.ayfaar.app.model.VideoResource;
import org.ayfaar.app.services.topics.TopicService;
import org.ayfaar.app.utils.GoogleService;
import org.ayfaar.app.utils.Language;
import org.springframework.boot.logging.LogLevel;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.ayfaar.app.utils.GoogleService.codeVideoPatternRegExp;

@Service
@Slf4j
public class GetVideosFormYoutube {

    @Inject CommonDao commonDao;
    @Inject TopicService topicService;
    @Inject EventPublisher publisher;

    @Scheduled(cron = "0 0 3 * * ?") // at 3 AM every day
    public void synchronize() throws IOException {

        final YouTube youtube = GoogleService.getYoutubeService();

        // https://www.googleapis.com/youtube/v3/channels?part=contentDetails&forUsername=PlanetaAyfaar3&key=AIzaSyDZ2HuAgqxzjXCyzlkhN67pXcYtW_WuVLk
        final String channelId = "UCbh7Mn8ri5PKx6mOridaHCA";// channel id for user PlanetaAyfaar3
        final String uploadsPlaylistId = "UUbh7Mn8ri5PKx6mOridaHCA";

        List<PlaylistItem> allVideos = new ArrayList<>();

        String pageToken = null;
        do {
            PlaylistItemListResponse sliceOfVideos = youtube.playlistItems()
                    .list("contentDetails")
                    .setMaxResults(50L)
                    .setPlaylistId(uploadsPlaylistId)
                    .setPageToken(pageToken)
                    .execute();
            allVideos.addAll(sliceOfVideos.getItems());
            pageToken = sliceOfVideos.getNextPageToken();
        } while (pageToken != null);

        List<VideoResource> addedVideos = new ArrayList<>();

        allVideos.stream()
                .map(PlaylistItem::getContentDetails)
                .map(PlaylistItemContentDetails::getVideoId)
                .forEach(videoId -> {
                    final Optional<VideoResource> opt = commonDao.getOpt(VideoResource.class, "id", videoId);
                    if (!opt.isPresent()) {
                        VideoListResponse response;
                        try {
                            response = youtube.videos().list("snippet ").set("id", videoId).execute();
                        } catch (IOException e) {
                            log.error("Error while getting data for youtube video id: " + videoId, e);
                            publisher.publishEvent(SysLogEvent.builder()
                                    .source("GetVideosFormYoutube")
                                    .message("Ошибка при получении данных с ютюба для видео " + videoId)
                                    .level(LogLevel.ERROR)
                                    .build());
                            return;
                        }
                        if (response.getItems().size() != 1) {
                            log.error("Size of response not 1 for youtube video id: " + videoId);
                            publisher.publishEvent(SysLogEvent.builder()
                                    .source("GetVideosFormYoutube")
                                    .message("При получении данных с ютюба для видео " + videoId + " количество записей не равно 1, а равно " + response.getItems().size())
                                    .level(LogLevel.ERROR)
                                    .build());
                            return;
                        }
                        final VideoSnippet snippet = response.getItems().get(0).getSnippet();
                        final VideoResource video = new VideoResource(videoId, Language.ru);
                        video.setTitle(snippet.getTitle());
                        video.setPublishedAt(new Date(snippet.getPublishedAt().getValue()));

                        snippet.getTags().forEach(tag -> {
                            Matcher matcher = Pattern.compile(codeVideoPatternRegExp).matcher(tag);
                            if (matcher.find()) {
                                video.setCode(matcher.group(0));
                            }
                        });

                        commonDao.save(video);

                        topicService.findOrCreate("нетегированные ответы", false, false).link(video);

                        addedVideos.add(video);
                    }
                });

        if (addedVideos.size() > 0) {
            String message = "Автоматически добавлены следующие видео ответы: ";
            message += StreamEx.of(addedVideos)
                    .map(v -> String.format("<uri label='%s'>%s</uri>", v.getTitle(), v.getUri()))
                    .joining(", ");
            publisher.publishEvent(SysLogEvent.builder()
                    .source("GetVideosFormYoutube")
                    .message(message)
                    .level(LogLevel.INFO)
                    .build());
        }
    }
}
