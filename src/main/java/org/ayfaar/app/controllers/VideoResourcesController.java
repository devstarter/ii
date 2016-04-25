package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.VideoResource;
import org.ayfaar.app.repositories.VideoResourceRepository;
import org.ayfaar.app.services.moderation.Action;
import org.ayfaar.app.services.moderation.ModerationService;
import org.ayfaar.app.utils.GoogleService;
import org.ayfaar.app.utils.Language;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

import static org.ayfaar.app.utils.GoogleService.extractVideoIdFromYoutubeUrl;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.util.Assert.hasLength;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("api/resource/video")
//todo: rename
public class VideoResourcesController {

    @Inject CommonDao commonDao;
    @Inject VideoResourceRepository videoResourceRepository;
    @Inject GoogleService youtubeService;
    @Inject ModerationService moderationService;

    @RequestMapping("{id}")
    public VideoResource get(@PathVariable String id) throws Exception {
        VideoResource video = commonDao.get(VideoResource.class, "id", id);
        if (video != null) {
            if (video.getTitle() == null) {
                final GoogleService.VideoInfo info = youtubeService.getVideoInfo(id);
                video.setTitle(info.title);
                video.setPublishedAt(info.publishedAt);
                commonDao.save(video);
            }
            return video;
        }
        return null;//commonDao.save(new VideoResource(id, Language.ru));
    }

    @RequestMapping("last-created")
    public List<VideoResource> lastCreated(@PageableDefault(size = 6, sort = "createdAt", direction = DESC) Pageable pageable) throws Exception {
        return commonDao.getPage(VideoResource.class, pageable);
    }

    @RequestMapping(method = POST)
    public VideoResource add(@RequestParam String url) throws Exception {
        hasLength(url);
        final String videoId = extractVideoIdFromYoutubeUrl(url);
        return commonDao.getOpt(VideoResource.class, "id", videoId).orElseGet(() -> {
            final GoogleService.VideoInfo info = youtubeService.getVideoInfo(videoId);
            final VideoResource video = new VideoResource(videoId, Language.ru);
            video.setTitle(info.title);
            video.setPublishedAt(info.publishedAt);
            AuthController.getCurrentUser().ifPresent(u -> video.setCreatedBy(u.getId()));
            commonDao.save(video);
            moderationService.notice(Action.VIDEO_ADDED, video.getTitle(), video.getUri());
            return video;
        });
    }

    @RequestMapping(value = "update-title", method = RequestMethod.POST)
    public void updateTitle(@RequestParam String uri, @RequestParam String title) {
        hasLength(uri);
        VideoResource video = commonDao.get(VideoResource.class, "uri", uri);
        if (video != null) {
            video.setTitle(title);
            commonDao.save(video);
        }
    }

}
