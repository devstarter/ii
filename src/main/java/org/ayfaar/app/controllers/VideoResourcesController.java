package org.ayfaar.app.controllers;

import org.ayfaar.app.annotations.Moderated;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.User;
import org.ayfaar.app.model.VideoResource;
import org.ayfaar.app.services.moderation.Action;
import org.ayfaar.app.services.moderation.ModerationService;
import org.ayfaar.app.utils.GoogleService;
import org.ayfaar.app.utils.Language;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

import static org.ayfaar.app.utils.GoogleService.extractVideoIdFromYoutubeUrl;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.util.Assert.hasLength;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping({"api/resource/video", "api/video"})
public class VideoResourcesController {

    private final CommonDao commonDao;
    private final GoogleService youtubeService;
    private final ModerationService moderationService;

    @Inject
    public VideoResourcesController(GoogleService youtubeService, CommonDao commonDao, ModerationService moderationService) {
        this.youtubeService = youtubeService;
        this.commonDao = commonDao;
        this.moderationService = moderationService;
    }

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
    @Moderated(value = Action.VIDEO_ADD, command = "@videoResourcesController.add")
    public VideoResource add(@RequestParam String url, @AuthenticationPrincipal User user) throws Exception {
        hasLength(url);
        final String videoId = extractVideoIdFromYoutubeUrl(url);
        return commonDao.getOpt(VideoResource.class, "id", videoId).orElseGet(() -> {
            final GoogleService.VideoInfo info = youtubeService.getVideoInfo(videoId);
            final VideoResource video = new VideoResource(videoId, Language.ru);
            video.setTitle(info.title);
            video.setPublishedAt(info.publishedAt);

            youtubeService.getCodeVideoFromYoutube(videoId).ifPresent(video::setCode);

            if (user != null) video.setCreatedBy(user.getId());
            commonDao.save(video);
            moderationService.notice(Action.VIDEO_ADDED, video.getTitle(), video.getUri());
            return video;
        });
    }

    @RequestMapping(value = "update-title", method = RequestMethod.POST)
    @Moderated(value = Action.VIDEO_UPDATE_TITLE, command = "@videoResourcesController.updateTitle")
    public void updateTitle(@RequestParam String uri, @RequestParam String title) {
        hasLength(uri);
        VideoResource video = commonDao.get(VideoResource.class, "uri", uri);
        if (video != null) {
            video.setTitle(title);
            commonDao.save(video);
        }
    }

    @RequestMapping("{id}/remove")
    @Moderated(value = Action.VIDEO_REMOVE, command = "@videoResourcesController.remove")
    public void remove(@PathVariable String id) {
        commonDao.getOpt(VideoResource.class, "id", id).ifPresent(video -> {
            commonDao.remove(video);
            moderationService.notice(Action.VIDEO_REMOVED, video.getTitle(), video.getId());
            // todo: update linked entities (topic links for example)
        });
    }

    @RequestMapping(value = "update-code", method = POST)
    @Moderated(value = Action.VIDEO_UPDATE_CODE, command = "@videoResourcesController.updateCode")
    public void updateCode(@RequestParam String id, @RequestParam String code) {
        commonDao.getOpt(VideoResource.class, "id", id).ifPresent(video -> {
            final String oldCode = video.getCode();
            video.setCode(code);
            moderationService.notice(Action.VIDEO_CODE_UPDATED, video.getTitle(), video.getUri(), oldCode != null ? oldCode : "<пусто>", code);
        });
    }
}
