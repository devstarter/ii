package org.ayfaar.app.services.topics;

import org.ayfaar.app.model.Topic;
import org.ayfaar.app.utils.UriGenerator;

import java.util.Optional;

public interface TopicService {
    Optional<TopicProvider> get(String uri);
    TopicProvider findOrCreate(String name);

    /**
     * Throw exception on topic not found
     * @param name
     * @return
     */
    default TopicProvider getByName(String name) {
        return get(UriGenerator.generate(Topic.class, name))
                .orElseThrow(() -> new RuntimeException("Topic for " + name + " not found"));
    }

    void reload();
}
