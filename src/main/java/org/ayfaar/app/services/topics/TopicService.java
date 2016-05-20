package org.ayfaar.app.services.topics;

import org.ayfaar.app.model.Topic;
import org.ayfaar.app.utils.UriGenerator;
import org.ayfaar.app.utils.exceptions.ExceptionCode;
import org.ayfaar.app.utils.exceptions.LogicalException;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public interface TopicService {
    @NotNull
    Optional<TopicProvider> get(String uri);

    @NotNull
    Optional<TopicProvider> get(String uri, boolean caseSensitive);

    @NotNull
    default TopicProvider findOrCreate(String name) {
        return findOrCreate(name, false);
    }

    @NotNull
    TopicProvider findOrCreate(String name, boolean caseSensitive);

    /**
     * Throw exception on topic not found
     * @param name
     * @return
     */
    @NotNull
    default TopicProvider getByName(String name){
        return get(UriGenerator.generate(Topic.class, name))
                .orElseThrow(() -> new LogicalException(ExceptionCode.TOPIC_NOT_FOUND, name));
    }

    @NotNull
    TopicProvider getByName(String name, boolean caseSensitive);

    void reload();


    boolean exist(String name);

    List<String> getAllNames();

    Map<String, String> getAllUriNames();
}