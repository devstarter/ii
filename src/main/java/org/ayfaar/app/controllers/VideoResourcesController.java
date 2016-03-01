package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.VideoResource;
import org.ayfaar.app.utils.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.util.Assert.hasLength;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("api/resource/video")
public class VideoResourcesController {

    @Autowired CommonDao commonDao;

    @RequestMapping("{id}")
    @ResponseBody
    public VideoResource get(@PathVariable String id) throws Exception {
        VideoResource video = commonDao.get(VideoResource.class, "id", id);
        if (video != null) return video;
        return null;//commonDao.save(new VideoResource(id, Language.ru));
    }

    @RequestMapping(method = POST)
    @ResponseBody
    public VideoResource add(@RequestParam String url) throws Exception {
        hasLength(url);
        final String videoId = extractIdFromUrl(url);
        VideoResource video = commonDao.get(VideoResource.class, "id", videoId);
        if (video != null) return video;
        return commonDao.save(new VideoResource(videoId, Language.ru));
    }

    private String extractIdFromUrl(String url) {
        //https://www.youtube.com/watch?v=044VwC_uptU
        Matcher matcher = Pattern.compile("^https://www\\.youtube\\.com/watch\\?v=([^&]+)").matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            // https://youtu.be/1I1cy6z-FgY
            matcher = Pattern.compile("^https://youtu.be/(.*)^").matcher(url);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        throw new RuntimeException("Cannot resolve video id");
    }
}
