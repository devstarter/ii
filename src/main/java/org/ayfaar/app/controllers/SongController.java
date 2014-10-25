package org.ayfaar.app.controllers;


import org.ayfaar.app.dao.SongDao;
import org.ayfaar.app.model.Song;
import org.ayfaar.app.spring.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.ayfaar.app.utils.ValueObjectUtils.getModelMap;

@Controller
@RequestMapping("api/song")
public class SongController {
    @Autowired SongDao songDao;

    @Model
    @ResponseBody
    @RequestMapping("{songId}")
    public ModelMap get(@PathVariable Integer songId) {
        Song song = songDao.getSongHtml(songId);
        ModelMap modelMap = (ModelMap) getModelMap(song);

        Song next = songDao.get(song.getId() + 1);
        if (next !=  null) {
            ModelMap nextMap = new ModelMap();
            nextMap.put("uri", "песня:"+next.getId());
            nextMap.put("id", next.getId());
            modelMap.put("next", nextMap);
        }

        Song prev = songDao.get(song.getId() - 1);
        if (prev !=  null) {
            ModelMap prevMap = new ModelMap();
            prevMap.put("uri", "песня:"+prev.getId());
            prevMap.put("id", prev.getId());
            modelMap.put("prev", prevMap);
        }

        return modelMap;
    }
}