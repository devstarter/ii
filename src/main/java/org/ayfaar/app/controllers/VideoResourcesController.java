package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.VideoResource;
import org.ayfaar.app.repositories.VideoResourceRepository;
import org.ayfaar.app.utils.Language;
import org.ayfaar.app.utils.GoogleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.util.List;

import static org.ayfaar.app.utils.GoogleService.extractVideoIdFromYoutubeUrl;
import static org.springframework.util.Assert.hasLength;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("api/resource/video")
public class VideoResourcesController {

    @Inject CommonDao commonDao;
    @Inject VideoResourceRepository videoResourceRepository;
    @Inject
    GoogleService youtubeService;

    @RequestMapping("{id}")
    @ResponseBody
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
    @ResponseBody
    public List<VideoResource> lastCreated() throws Exception {
        /*return videoResourceRepository
                .findAll(new PageRequest(0, 8, new Sort(Sort.Direction.DESC, "createdAt")))
                .getContent();*/
        return commonDao.getOrdered(VideoResource.class, "createdAt", false, 8);
    }

    @RequestMapping(method = POST)
    @ResponseBody
    public VideoResource add(@RequestParam String url) throws Exception {
        hasLength(url);
        final String videoId = extractVideoIdFromYoutubeUrl(url);
        VideoResource video = commonDao.get(VideoResource.class, "id", videoId);
        if (video != null) return video;
        final GoogleService.VideoInfo info = youtubeService.getVideoInfo(videoId);
        video = new VideoResource(videoId, Language.ru);
        video.setTitle(info.title);
        video.setPublishedAt(info.publishedAt);
        return commonDao.save(video);
    }

}
