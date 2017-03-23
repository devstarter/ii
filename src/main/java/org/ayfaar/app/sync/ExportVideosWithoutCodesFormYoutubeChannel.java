package org.ayfaar.app.sync;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemContentDetails;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.VideoResource;
import org.ayfaar.app.services.GoogleSpreadsheetService;
import org.ayfaar.app.utils.GoogleService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.lang.StringUtils.isEmpty;

@RestController
@RequestMapping("api/export-no-code-videos")
public class ExportVideosWithoutCodesFormYoutubeChannel {

    @Inject CommonDao commonDao;
    @Inject GoogleSpreadsheetService spreadsheetService;

    @RequestMapping
    public void export() throws IOException {

        final YouTube youtube = GoogleService.getYoutubeService();

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

        List<VideoResource> videosWithoutCodes = new ArrayList<>();

        allVideos.stream()
                .map(PlaylistItem::getContentDetails)
                .map(PlaylistItemContentDetails::getVideoId)
                .forEach(videoId -> {
                    final Optional<VideoResource> opt = commonDao.getOpt(VideoResource.class, "id", videoId);
                    final VideoResource video = opt.get();
                    if (!video.getOfficial()) {
                        video.setOfficial(true);
                        commonDao.save(video);
                    }

                    if (isEmpty(video.getCode())) videosWithoutCodes.add(video);
                });

        GoogleSpreadsheetSynchronizer<VideoSyncData> synchronizer = GoogleSpreadsheetSynchronizer.<VideoSyncData>build(spreadsheetService, "1FQMsePNcMBKDibwe_BeqGcJnntXScx2YcxJmy8w_rGw")
                .keyGetter(VideoSyncData::url)
                .localDataLoader(() -> videosWithoutCodes.stream()
                        .map(v -> VideoSyncData.builder()
                            .name(v.getTitle())
                            .url("https://youtu.be/" + v.getId())
                            .build())
                        .collect(Collectors.toList()))
                .build();
        synchronizer.sync();
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    @Builder
    @ToString
    private static class VideoSyncData implements SyncItem {
        public String name;
        public String url;

        @Override
        public List<Object> toRaw() {
            final ArrayList<Object> obj = new ArrayList<>();
            obj.add(name);
            obj.add(url);
            return obj;
        }
    }
}
