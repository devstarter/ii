package org.ayfaar.app.services.topics;

import lombok.Builder;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.LinkType;
import org.ayfaar.app.model.Topic;
import org.ayfaar.app.model.UID;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public interface TopicProvider {
    String name();

    default void link(LinkType type, UID uid) {
        link(type, uid, null);
    }

    default Link link(LinkType type, UID uid, String comment) {
        return link(type, uid, comment, null, null);
    }

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
    TopicResources resources();

    default void link(UID uid) {
        link(null, uid);
    }

    Link link(LinkType linkType, UID uid, String comment, String quote, Float rate);

    class TopicResources {
        public List<ResourcePresentation> video = new LinkedList<>();
        public List<ResourcePresentation> item = new LinkedList<>();
    }

    class ResourcePresentation {
        public String quote;
        public String comment;
        public Float rate;
        public Resource resource;

        ResourcePresentation(UID uid, Link link) {
            quote = link.getQuote();
            comment = link.getComment();
            rate = link.getRate();
            resource = Resource.builder().title(uid.toTitle()).uri(uid.getUri()).build();
        }

        @Builder
        static class Resource {
            public String uri;
            public String title;
        }
    }
}
