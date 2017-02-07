package org.ayfaar.app.services.videoResource;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.UID;
import org.ayfaar.app.model.VideoResource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class VideoResourceService {
    @Inject CommonDao commonDao;

    public Map<String, String> getAllUriNames() {
        return getAll().stream().collect(Collectors.toMap(UID::getUri, VideoResource::getTitle));
    }

    public List<VideoResource> getAll() {
        return commonDao.getAll(VideoResource.class);
    }
}
