package org.ayfaar.app.services.topics;

import one.util.streamex.StreamEx;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.model.*;
import org.ayfaar.app.services.links.LinkService;
import org.ayfaar.app.services.moderation.Action;
import org.ayfaar.app.services.moderation.ModerationService;
import org.ayfaar.app.services.moderation.UserRole;
import org.ayfaar.app.utils.CurrentUserProvider;
import org.ayfaar.app.utils.UriGenerator;
import org.ayfaar.app.utils.exceptions.ExceptionCode;
import org.ayfaar.app.utils.exceptions.LogicalException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static one.util.streamex.MoreCollectors.onlyOne;
import static org.ayfaar.app.utils.StreamUtils.single;

@Component("topicService")
class TopicServiceImpl implements TopicService {
    private static final Logger logger = LoggerFactory.getLogger(TopicServiceImpl.class);

    /**
     * Key is topic uri in lower case
     */
    private TopicsMap topics = new TopicsMap();

    @Inject CommonDao commonDao;
    @Inject LinkDao linkDao;
    @Inject ModerationService moderationService;
    @Inject Environment environment;
    @Inject CurrentUserProvider currentUserProvider;
    @Inject LinkService linkService;

    @PostConstruct
    private void init() {
        logger.info("Topics loading...");

        commonDao.getAll(Topic.class)
                .stream()
//                .parallel()
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

    @NotNull
    @Override
    public Optional<TopicProvider> get(String uri) {
        return Optional.ofNullable(topics.get(uri));
    }

    @NotNull
    @Override
    public Optional<TopicProvider> get(String uri, boolean caseSensitive) {
        if (!caseSensitive) return get(uri);
        return topics.values().parallelStream()
                .filter(p -> p.uri().equals(uri))
                .collect(single());
    }

    @NotNull
    @Override
    public TopicProvider findOrCreate(String name, boolean caseSensitive) {
        return get(UriGenerator.generate(Topic.class, name), caseSensitive)
                .orElseGet(() -> {
                    moderationService.check(Action.TOPIC_CREATE, name);
                    final Topic topic = commonDao.save(new Topic(name));
                    final TopicProviderImpl provider = new TopicProviderImpl(topic);
                    topics.put(provider.uri(), provider);
                    return provider;
                });
    }

    @NotNull
    @Override
    public TopicProvider getByName(String name, boolean caseSensitive) {
        if (!caseSensitive) return getByName(name);
        return get(UriGenerator.generate(Topic.class, name), true)
                .orElseThrow(() -> new LogicalException(ExceptionCode.TOPIC_NOT_FOUND, name));
    }

    @Override
    public void reload() {
        topics.clear();
        init();
    }

    @Override
    public boolean exist(String name) {
        return topics.values().stream().anyMatch(c -> c.name().equals(name));
    }

    @Override
    // fixme: sorting by Link.rate DESC
    public StreamEx<TopicProvider> getAllTopicsLinkedWith(String uri){
        return StreamEx.of(topics.values())
                .flatMap(topicProvider -> StreamEx.of(topicProvider.linksMap.values())
                        .filter(link -> link.getUid2().getUri().equals(uri))
                        .sortedByDouble(Link::getRate)
                        .reverseSorted()
                        .map(link -> get(link.getUid1().getUri()).get()));
    }
    @Override
    public List<String> getAllNames(){
        return topics.values().stream().map(topicProvider ->
                topicProvider.topic().getName()).collect(Collectors.toList());
    }
    @Override
    public Map<String, String> getAllUriNames(){
        return topics.values().stream().collect(Collectors.toMap(topicProvider ->
                topicProvider.topic().getUri(),topicProvider -> topicProvider.topic().getName()));
    }

    private class TopicProviderImpl implements TopicProvider {
        private final Topic topic;
        private Map<UID, Link> linksMap = new HashMap<>();

        private TopicProviderImpl(Topic topic) {
            Assert.notNull(topic);
            this.topic = topic;
        }

        @NotNull
        @Override
        public String name() {
            return topic.getName();
        }

        @Override
        public Link link(LinkType type, UID uid, String comment, String quote, Float rate) {
            Link link = linksMap.get(uid);
            if (link != null && link.getType()!= null && !link.getType().isChild())
                throw new RuntimeException("Link already exist with another type: " + link.getType());

            // link = linkRepository.save(new Link(topic, uid, type, comment)); this throw error

            link = Link.builder()
                    .uid1(topic)
                    .uid2(uid)
                    .type(type)
                    .comment(comment)
                    .quote(quote)
                    .rate(rate)
                    .build();
            Link finalLink = link;

//            if (!environment.acceptsProfiles("dev"))
            currentUserProvider.get().ifPresent(u -> finalLink.setCreatedBy(u.getId()));

            if (uid instanceof Topic) {
                topics.get(uid.getUri()).registerLink(link, topic);
            }
            linkDao.save(link);
            linkService.registerNew(link);
            registerLink(link, uid);
            return link;
        }

        @Override
        public Optional<? extends TopicProvider> getChild(String child) {
            return children().filter(p -> p.name().equals(child)).collect(onlyOne());
        }

        @Override
        public Stream<? extends TopicProvider> children() {
            return StreamEx.of(linksMap.values())
                    .filter(link ->
                            link.getUid1() instanceof Topic
                            && link.getUid2() instanceof Topic
                            && link.getUid1().getUri().equals(uri())
                            && link.getType() == LinkType.CHILD)
                    .map(l -> new TopicProviderImpl((Topic) l.getUid2()))
                    .sorted((o1, o2) -> o1.name().toLowerCase().compareTo(o2.name().toLowerCase()));
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

        @NotNull
        @Override
        public Topic topic() {
            return topic;
        }

        @NotNull
        @Override
        public String uri() {
            return topic.getUri();
        }

        @Override
        public TopicProvider addChild(String name) {
            final TopicProvider child = findOrCreate(name);
            addChild(child);
            return child;
        }

        @Override
        public TopicProvider unlink(String linkedTopicName) {
            final TopicProvider linkedTopic = getByName(linkedTopicName);
            final Link link = linksMap.remove(linkedTopic.topic());
            linkDao.remove(link.getLinkId());
            return linkedTopic;
        }

        @Override
        public void delete() {
            commonDao.remove(topic);
            topics.remove(topic().getUri());
        }

        @Override
        public TopicProviderImpl merge(String mergeInto) {
            moderationService.check(Action.TOPIC_MERGE, topic.getName(), mergeInto);
            commonDao.remove(topic); // remove from db for case sensitive case
            final TopicProvider provider = findOrCreate(mergeInto, true);
            linksMap.values().forEach(link -> {
                // заменяем ссылки на старый топик на ссылки на новый
                UID uid1 = link.getUid1().getUri().equals(uri()) ? provider.topic() : link.getUid1();
                UID uid2 = link.getUid2().getUri().equals(uri()) ? provider.topic() : link.getUid2();
                link = linkDao.save(Link.builder()
                        .uid1(uid1)
                        .uid2(uid2)
                        .type(link.getType())
                        .comment(link.getComment())
                        .quote(link.getQuote())
                        .rate(link.getRate())
                        .build());
            });
            moderationService.notice(Action.TOPIC_MERGED, topic.getName(), provider.name());
            reload();
            return this;
        }

        @Override
        public TopicResources resources() {
            final TopicResources resources = new TopicResources();
            resources.image.addAll(prepareResource(Image.class));
            resources.video.addAll(prepareResource(VideoResource.class));
            resources.item.addAll(prepareItemResource());
            resources.itemsRange.addAll(prepareResource(ItemsRange.class));
            resources.document.addAll(prepareResource(Document.class));
            resources.record.addAll(StreamEx.of(prepareResource(Record.class))
                    .filter(r -> r.resource.getAudioUrl() != null ||
                            (currentUserProvider.get().isPresent() && currentUserProvider.get().get().getRole().accept(UserRole.ROLE_EDITOR)))
                    .sorted((r1, r2) -> {
                        if (r1.resource.getAudioUrl() != null && r2.resource.getAudioUrl() == null) return -1;
                        if (r2.resource.getAudioUrl() != null && r1.resource.getAudioUrl() == null) return 1;
                        return r2.resource.getRecorderAt().compareTo(r1.resource.getRecorderAt());
                    }).toList());
            return resources;
        }

        private Collection<ResourcePresentation<ItemResourcePresentation>> prepareItemResource() {
            List<ResourcePresentation<ItemResourcePresentation>> list = new LinkedList<>();
            //noinspection unchecked
            linksMap.entrySet().stream()
                    .filter(e -> e.getKey() instanceof Item)
                    .map(e -> new ResourcePresentation(new ItemResourcePresentation((Item) e.getKey()), e.getValue()))
                    .forEach(list::add);
            return list;
        }

        private <T> List<ResourcePresentation<T>> prepareResource(Class<T> resourceClass) {
            List<ResourcePresentation<T>> list = new LinkedList<>();
            //noinspection unchecked
            linksMap.entrySet().stream()
                    .filter(e -> e.getKey().getClass().isAssignableFrom(resourceClass))
                    .map(e -> new ResourcePresentation(e.getKey(), e.getValue()))
                    .sorted()
                    .forEachOrdered(list::add);
            return list;
        }

        private void registerLink(Link link, UID uid) {
            //todo: не хранить тут саму сущность UID, а хранить только пё презентацию которая будет выдана в ресурсках
            linksMap.put(uid, link);
        }

        private class ItemResourcePresentation {
            public final String number;
            public final String uri;

            private ItemResourcePresentation(Item item) {
                number = item.getNumber();
                uri = item.getUri();
            }
        }
    }

    private class TopicsMap extends LinkedHashMap<String, TopicProviderImpl> {
        @Override
        public TopicProviderImpl put(String key, TopicProviderImpl value) {
            return super.put(key.toLowerCase(), value);
        }

        @Override
        public TopicProviderImpl get(Object key) {
            return super.get(((String) key).toLowerCase());
        }
    }
}