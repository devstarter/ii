package org.ayfaar.app.services;

import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.LinkType;
import org.ayfaar.app.model.Topic;
import org.ayfaar.app.model.UID;
import org.ayfaar.app.repositories.TopicRepository;
import org.ayfaar.app.utils.UriGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Component
class TopicServiceImpl implements TopicService {
    private static final Logger logger = LoggerFactory.getLogger(TopicServiceImpl.class);

    private LinkedHashMap<String, TopicProvider> topics = new LinkedHashMap<>();

    @Inject TopicRepository topicRepository;
    @Inject LinkDao linkDao;

    @PostConstruct
    private void init() {
        logger.info("Topics loading...");

        topicRepository.findAll()
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
                        final TopicProviderImpl provider = (TopicProviderImpl) topics.get(link.getUid1().getUri());
                        provider.addLoadedLink(link, link.getUid2());
                    }
                    if (link.getUid2() instanceof Topic) {
                        final TopicProviderImpl provider = (TopicProviderImpl) topics.get(link.getUid2().getUri());
                        provider.addLoadedLink(link, link.getUid1());
                    }
                });
        logger.info("Topics loaded");
    }

    @Override
    public Optional<TopicProvider> get(String uri) {
        return Optional.ofNullable(topics.get(uri));
    }

    @Override
    public TopicProvider getOrCreate(String name) {
        return Optional.ofNullable(topics.get(UriGenerator.generate(Topic.class, name)))
                .orElseGet(() -> {
                    final Topic topic = topicRepository.save(new Topic(name));
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
                addLoadedLink(link, uid);
            }

            return link;
        }

        @Override
        public List<Topic> children() {
            return linksMap.values().stream()
                    .filter(l ->
                            l.getUid1() instanceof Topic
                            && l.getUid2() instanceof Topic
                            && l.getUid1().getUri().equals(uri())
                            && l.getType().isChild())
                    .map(l -> (Topic) l.getUid2())
                    .collect(toList());
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
            addChild(getOrCreate(name));
        }

        private void addLoadedLink(Link link, UID uid) {
            linksMap.put(uid, link);
        }
    }
}
