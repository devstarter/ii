package org.ayfaar.app.repositories;

import org.ayfaar.app.model.VideoResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoResourceRepository extends JpaRepository<VideoResource, String> {
}
