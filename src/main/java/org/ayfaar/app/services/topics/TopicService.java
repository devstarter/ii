package org.ayfaar.app.services.topics;

import one.util.streamex.StreamEx;
import org.ayfaar.app.model.Topic;
import org.ayfaar.app.utils.UriGenerator;
import org.ayfaar.app.utils.exceptions.ExceptionCode;
import org.ayfaar.app.utils.exceptions.LogicalException;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TopicService {
    @NotNull
    Optional<TopicProvider> get(String uri);

    @NotNull
    Optional<TopicProvider> contains(String s);

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
        return findByName(name).orElseThrow(() -> new LogicalException(ExceptionCode.TOPIC_NOT_FOUND, name));
    }

    default Optional<TopicProvider> findByName(String name){
        return get(UriGenerator.generate(Topic.class, name));
    }

    @NotNull
    TopicProvider getByName(String name, boolean caseSensitive);

    void reload();


    boolean exist(String name);

    StreamEx<TopicProvider> getAllLinkedWith(String uri);

    List<String> getAllNames();

    Map<String, String> getAllUriNames();
}