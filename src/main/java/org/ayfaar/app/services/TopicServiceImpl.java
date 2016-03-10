package org.ayfaar.app.services;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.LinkType;
import org.ayfaar.app.model.Topic;
import org.ayfaar.app.model.UID;
import org.ayfaar.app.utils.UriGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Stream;

@Component
class TopicServiceImpl implements TopicService {
    private static final Logger logger = LoggerFactory.getLogger(TopicServiceImpl.class);

    private LinkedHashMap<String, TopicProviderImpl> topics = new LinkedHashMap<>();

    @Inject CommonDao commonDao;
    @Inject LinkDao linkDao;

    @PostConstruct
    private void init() {
        logger.info("Topics loading...");

        commonDao.getAll(Topic.class)
                .stream()
                .parallel()
                .map(TopicProviderImpl::new)
                .forEach(t -> topics.put(t.uri(), t));

        linkDao.getAll()
                .stream()
                .parallel()
                .filter(l -> l.getUid1() instanceof Topic || l.getUid2() instanceof Topic)
                .forEach(link -> {
                    if (link.getUid1() instanceof Topic) {
                        final TopicProviderImpl provider = topics.get(link.getUid1().getUri());
                        provider.registerLink(link, link.getUid2());
                    }
                    if (link.getUid2() instanceof Topic) {
                        final TopicProviderImpl provider = topics.get(link.getUid2().getUri());
                        provider.registerLink(link, link.getUid1());
                    }
                });
        logger.info("Topics loaded");
    }

    @Override
    public Optional<TopicProvider> get(String uri) {
        return Optional.ofNullable(topics.get(uri));
    }

    @Override
    public TopicProvider findOrCreate(String name) {
        return Optional.ofNullable(topics.get(UriGenerator.generate(Topic.class, name)))
                .orElseGet(() -> {
                    final Topic topic = commonDao.save(new Topic(name));
                    final TopicProviderImpl provider = new TopicProviderImpl(topic);
                    topics.put(provider.uri(), provider);
                    return provider;
                });
    }

    private class TopicProviderImpl implements TopicProvider {
        private final Topic topic;
        private Map<UID, Link> linksMap = new HashMap<>();

        private TopicProviderImpl(Topic topic) {
            this.topic = topic;
        }

        @Override
        public String name() {
            return topic.getName();
        }

        @Override
        public Link link(LinkType type, UID uid, String comment) {
            Link link = linksMap.get(uid);
            if (link != null && link.getType() != type)
                throw new RuntimeException("Link already exist with another type: "+link.getType());

            if (link == null) {
                // link = linkRepository.save(new Link(topic, uid, type, comment)); this throw error
                link = linkDao.save(new Link(topic, uid, type, comment));
                if (uid instanceof Topic) {
                    topics.get(uid.getUri()).registerLink(link, topic);
                }
                registerLink(link, uid);
            }

            return link;
        }

        @Override
        public Stream<TopicProvider> children() {
            return linksMap.values().stream()
                    .filter(link ->
                            link.getUid1() instanceof Topic
                            && link.getUid2() instanceof Topic
                            && link.getUid1().getUri().equals(uri())
                            && link.getType().isChild())
                    .map(l -> new TopicProviderImpl((Topic) l.getUid2()));
        }

        @Override
        public Stream<TopicProvider> parents() {
            return linksMap.values().stream()
                    .filter(link ->
                            link.getUid1() instanceof Topic
                            && link.getUid2() instanceof Topic
                            && link.getUid2().getUri().equals(uri())
                            && link.getType().isChild())
                    .map(l -> new TopicProviderImpl((Topic) l.getUid1()));
        }

        @Override
        public Stream<TopicProvider> related() {
            return linksMap.entrySet().stream()
                    .filter(entry -> entry.getKey() instanceof Topic && entry.getValue().getType() == null)
                    .map(e -> new TopicProviderImpl((Topic) e.getKey()));
        }



        @Override
        public Topic topic() {
            return topic;
        }

        @Override
        public String uri() {
            return topic.getUri();
        }

        @Override
        public void addChild(String name) {
            addChild(findOrCreate(name));
        }

        @Override
        public Stream<TopicResourcesGroup> resources() {
            // todo: находим линки не относящиеся к топикам и сортируем по типу (пока только один тип VideoResource)
            throw new RuntimeException("Unimplemented"); // удалить когда добавишь нужную логику
        }

        private void registerLink(Link link, UID uid) {
            linksMap.put(uid, link);
        }
    }
}