package org.ayfaar.app.utils;

import org.ayfaar.app.model.Term;
import org.ayfaar.app.model.Topic;

import java.util.List;
import java.util.Map;

/**
 * �?нтерфейс взаимодействия с предварительно загруженными всеми топиками
 */
public interface TopicService {
    /**
     *
     *
     */
    List<Map.Entry<String, TopicProvider>> getAll();
    TopicProvider getTopicProvider(String name);
    Topic getTopic(String name);
    //void reload();

    interface TopicProvider {
        String getName();
        String getUri();
        //boolean hasShortDescription();
        TopicProvider getMainTopicProvider();
        Topic getTopic();

//        List<TopicProvider> getAliases();

//        TopicProvider getCode();
        Byte getType();
        boolean hasMainTopic();

//        boolean isAlias();
//        boolean isCode();
        //boolean hasCode();
        //List<String> getAllAliasesWithAllMorphs();
    }
}
