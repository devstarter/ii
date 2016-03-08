package org.ayfaar.app.utils;

import org.ayfaar.app.model.Term;
import org.ayfaar.app.model.Topic;

import java.util.List;
import java.util.Map;

/**
 * 
 */
public interface TopicService {
    /**
     *
     *
     */
    List<Map.Entry<String, TopicProvider>> getAll();
    TopicProvider getTopicProvider(String name);
    Topic getTopic(String name);
    void reload();

    interface TopicProvider {
        String getName();
        String getUri();

        Topic getTopic();

        List<TopicProvider> getParents();

        List<TopicProvider> getChildren();

        List<TopicProvider> getSimilar();

        Byte getType();
        boolean hasMainTopic();


    }
}
