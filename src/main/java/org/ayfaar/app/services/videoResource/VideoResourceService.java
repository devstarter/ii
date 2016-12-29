package org.ayfaar.app.services.videoResource;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.UID;
import org.ayfaar.app.model.VideoResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class VideoResourceService {
    @Inject CommonDao commonDao;

    public Map<String, String> getAllUriNames() {
        return getAll().collect(Collectors.toMap(UID::getUri, VideoResource::getTitle));
    }

    public Stream<VideoResource> getAll() {
        return commonDao.getAll(VideoResource.class).stream();
    }
}
