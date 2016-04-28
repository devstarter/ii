package org.ayfaar.app.services.videoResource;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.VideoResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.stream.Collectors;

@Component()
public class VideoResourceServiceImpl implements VideoResourceService {
    @Autowired
    CommonDao commonDao;

    @Override
    public Map<String, String> getAllUriNames() {
        return commonDao.getAll(VideoResource.class).stream().collect(Collectors.toMap(videoResource ->
                videoResource.getUri(),videoResource -> videoResource.getTitle()));
    }
}
