package org.ayfaar.app.services;

import org.ayfaar.app.model.*;

import java.util.List;
import java.util.stream.Stream;

public interface TopicProvider {
    String name();

    default void link(LinkType type, UID uid) {
        link(type, uid, null);
    }

    Link link(LinkType type, UID uid, String comment);

    Stream<TopicProvider> children();
    Stream<TopicProvider> parents();
    Stream<TopicProvider> related();

    default void addChild(Topic child) {
        link(LinkType.CHILD, child);
    }

    default void addChild(TopicProvider topicProvider) {
        addChild(topicProvider.topic());
    }

    Topic topic();

    String uri();

    void addChild(String name);

    /**
     * @return все ресурсы (пока только VideoResource) связаные любыми линками с этой темой
     */
    Stream<TopicResourcesGroup> resources();

    class TopicResourcesGroup {
        public ResourceType type;
        public List<UID> resources;
    }


    //void addParent(String parent);

}
