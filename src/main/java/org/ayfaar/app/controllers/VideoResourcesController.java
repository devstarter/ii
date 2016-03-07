package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.VideoResource;
import org.ayfaar.app.repositories.VideoResourceRepository;
import org.ayfaar.app.utils.Language;
import org.ayfaar.app.utils.YoutubeService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.util.Assert.hasLength;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("api/resource/video")
public class VideoResourcesController {

    @Inject CommonDao commonDao;
    @Inject VideoResourceRepository videoResourceRepository;
    @Inject YoutubeService youtubeService;

    @RequestMapping("{id}")
    @ResponseBody
    public VideoResource get(@PathVariable String id) throws Exception {
        VideoResource video = commonDao.get(VideoResource.class, "id", id);
        if (video != null) {
            if (video.getTitle() == null) {
                final YoutubeService.VideoInfo info = youtubeService.getInfo(id);
                video.setTitle(info.title);
                video.setPublishedAt(info.publishedAt);
                commonDao.save(video);
            }
            return video;
        }
        return null;//commonDao.save(new VideoResource(id, Language.ru));
    }

    @RequestMapping("last-ten")
    @ResponseBody
    public List<VideoResource> lastTen() throws Exception {
        return videoResourceRepository
                .findAll(new PageRequest(0, 8, new Sort(Sort.Direction.DESC, "createdAt")))
                .getContent();
//        return commonDao.getOrdered(VideoResource.class, "createdAt", false, 8);
    }

    @RequestMapping(method = POST)
    @ResponseBody
    public VideoResource add(@RequestParam String url) throws Exception {
        hasLength(url);
        final String videoId = extractIdFromUrl(url);
        VideoResource video = commonDao.get(VideoResource.class, "id", videoId);
        if (video != null) return video;
        final YoutubeService.VideoInfo info = youtubeService.getInfo(videoId);
        video = new VideoResource(videoId, Language.ru);
        video.setTitle(info.title);
        video.setPublishedAt(info.publishedAt);
        return commonDao.save(video);
    }

    private String extractIdFromUrl(String url) {
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
}