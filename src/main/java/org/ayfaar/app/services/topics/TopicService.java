package org.ayfaar.app.services.topics;

import org.ayfaar.app.model.Topic;
import org.ayfaar.app.utils.UriGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface TopicService {
    @NotNull
    Optional<TopicProvider> get(String uri);
    @NotNull
    TopicProvider findOrCreate(String name);

    /**
     * Throw exception on topic not found
     * @param name
     * @return
     */
    @NotNull
    default TopicProvider getByName(String name) {
        return get(UriGenerator.generate(Topic.class, name))
                .orElseThrow(() -> new RuntimeException("Topic for " + name + " not found"));
    }

    void reload();


    boolean hasTopic(String name);
}
