package org.ayfaar.app.dao;


import org.ayfaar.app.model.VideoResource;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VideoResourceDao extends BasicCrudDao<VideoResource>{

    List<VideoResource> get(String nameOrCode, String year, Pageable pageable);
}
