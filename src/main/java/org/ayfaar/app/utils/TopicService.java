package org.ayfaar.app.utils;

import org.ayfaar.app.model.Term;
import org.ayfaar.app.model.Topic;

import java.util.List;
import java.util.Map;

/**
<<<<<<< 9eac8854f5e7e4371177c7fe232e96cd3ecee253
 * 
=======
 * �?нтерфейс взаимодействия с предварительно загруженными всеми топиками
>>>>>>> add files TopicService by utils
 */
public interface TopicService {
    /**
     *
     *
     */
    List<Map.Entry<String, TopicProvider>> getAll();
    TopicProvider getTopicProvider(String name);
    Topic getTopic(String name);
<<<<<<< 9eac8854f5e7e4371177c7fe232e96cd3ecee253
    void reload();
=======
    //void reload();
>>>>>>> add files TopicService by utils

    interface TopicProvider {
        String getName();
        String getUri();
<<<<<<< 9eac8854f5e7e4371177c7fe232e96cd3ecee253

        Topic getTopic();

        List<TopicProvider> getParents();

        List<TopicProvider> getChildren();

        List<TopicProvider> getSimilar();

        Byte getType();
        boolean hasMainTopic();


=======
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
>>>>>>> add files TopicService by utils
    }
}
