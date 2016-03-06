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
          return get(UriGenerator.generate(Topic.class, name))
                .orElseThrow(() -> new TopicNotFoundException("Topic for " + name + " not found", "TOPIC_NOT_FOUND"));
                                    //new LogicalException(Exceptions.TOPIC_NOT_FOUND));
    }

    void reload();


    boolean hasTopic(String name);
}
