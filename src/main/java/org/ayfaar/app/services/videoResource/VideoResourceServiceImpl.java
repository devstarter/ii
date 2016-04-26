package org.ayfaar.app.services.videoResource;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.VideoResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("videoResourceService")
public class VideoResourceServiceImpl implements VideoResourceService {
    @Autowired
    CommonDao commonDao;
    @Override
    public List<String> getAll() {

        return commonDao.getAll(VideoResource.class).stream().map(v -> v.getTitle()).collect(Collectors.toList());
    }
}
