package org.ayfaar.app.services.topics;

import lombok.Builder;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.LinkType;
import org.ayfaar.app.model.Topic;
import org.ayfaar.app.model.UID;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public interface TopicProvider {
    @NotNull
    String name();

    default void link(LinkType type, UID uid) {
        link(type, uid, null);
    }

    default Link link(LinkType type, UID uid, String comment) {
        return link(type, uid, comment, null, null);
    }

    Stream<? extends TopicProvider> children();
    Stream<TopicProvider> parents();
    Stream<TopicProvider> related();

    default void addChild(Topic child) {
        link(LinkType.CHILD, child);
    }

    default void addChild(TopicProvider topicProvider) {
        addChild(topicProvider.topic());
    }

    @NotNull
    Topic topic();

    @NotNull
    String uri();

    void addChild(String name);
    void unlink(String linked);
    default void unlink(TopicProvider linked) {
        unlink(linked.name());
    }

    TopicProvider merge(String mergeWith);
    void delete();
    /**
     * @return все ресурсы связаные любыми линками с этой темой
     */
    // todo: return only 6 resources for each type and create new method fo loading rest with paging.
    // And return flag hasMore in each ResourcePresentation
    TopicResources resources();

    default void link(UID uid) {
        link(null, uid);
    }

    Link link(LinkType linkType, UID uid, String comment, String quote, Float rate);


    class TopicResources {
        public List<ResourcePresentation> video = new LinkedList<>();
        public List<ResourcePresentation> item = new LinkedList<>();
        public List<ResourcePresentation> itemsRange = new LinkedList<>();
        public List<ResourcePresentation> document = new LinkedList<>();
    }

    class ResourcePresentation<T> implements Comparable<ResourcePresentation<T>> {
        public String quote;
        public String comment;
        public Float rate;
        public T resource;

        ResourcePresentation(T uid, Link link) {
            quote = link.getQuote();
            comment = link.getComment();
            rate = link.getRate();
            resource = uid;
        }

        @Override
        public int compareTo(ResourcePresentation<T> o) {
            return resource instanceof Comparable ? ((Comparable<T>) resource).compareTo(o.resource) : 0;
        }

        @Builder
        static class Resource {
            public String uri;
            public String title;
        }
    }
}
