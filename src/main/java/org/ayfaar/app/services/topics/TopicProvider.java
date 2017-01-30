package org.ayfaar.app.services.topics;

import lombok.Builder;
import org.ayfaar.app.model.*;
import org.ayfaar.app.utils.TermService;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
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

    TopicProvider addChild(String name);
    TopicProvider unlink(String linkedTopicName);
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

    Optional<? extends TopicProvider> getChild(String child);

    class TopicResources {
        public List<ResourcePresentation> image = new LinkedList<>();
        public List<ResourcePresentation> video = new LinkedList<>();
        public List<ResourcePresentation> item = new LinkedList<>();
        public List<ResourcePresentation> itemsRange = new LinkedList<>();
        public List<ResourcePresentation> document = new LinkedList<>();
        public List<ResourcePresentation> record = new LinkedList<>();
    }

    class ResourcePresentation<T extends HasUri> implements Comparable<ResourcePresentation<T>> {
        public String quote;
        public String comment;
        public Float rate;
        public T resource;
        public List<String> topics;

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

    Optional<TermService.TermProvider> linkedTerm();

    // todo implement
    default boolean hasGrandParent(String name) {
        throw new RuntimeException("Unimplemented");
    }
}
