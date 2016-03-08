package org.ayfaar.app.services;

import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.LinkType;
import org.ayfaar.app.model.Topic;
import org.ayfaar.app.model.UID;

import java.util.List;

public interface TopicProvider {
    String name();

    default void link(LinkType type, UID uid) {
        link(type, uid, null);
    }

    Link link(LinkType type, UID uid, String comment);

    List<Topic> children();

    default void addChild(Topic child) {
        link(LinkType.CHILD, child);
    }

    default void addChild(TopicProvider topicProvider) {
        addChild(topicProvider.topic());
    }

    Topic topic();

    String uri();

    void addChild(String name);
}
