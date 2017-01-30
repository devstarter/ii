package org.ayfaar.app.controllers;

import lombok.Builder;
import org.ayfaar.app.annotations.Moderated;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.model.ItemsRange;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Topic;
import org.ayfaar.app.model.UID;
import org.ayfaar.app.services.links.LinkService;
import org.ayfaar.app.services.moderation.Action;
import org.ayfaar.app.services.moderation.ModerationService;
import org.ayfaar.app.services.topics.TopicProvider;
import org.ayfaar.app.services.topics.TopicService;
import org.ayfaar.app.translation.TopicTranslationSynchronizer;
import org.ayfaar.app.utils.TermService;
import org.ayfaar.app.utils.UriGenerator;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.springframework.util.Assert.hasLength;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("api/topic")
public class TopicController {

    final CommonDao commonDao;
    private final LinkDao linkDao;
    private final TopicService topicService;
    private final ModerationService moderationService;
    private final LinkService linkService;
    private final NewSuggestionsController suggestionsController;
    private TopicTranslationSynchronizer translationSynchronizer;

    @Inject
    public TopicController(TopicService topicService, NewSuggestionsController suggestionsController, ModerationService moderationService, LinkService linkService, CommonDao commonDao, LinkDao linkDao, TopicTranslationSynchronizer translationSynchronizer) {
        this.topicService = topicService;
        this.suggestionsController = suggestionsController;
        this.moderationService = moderationService;
        this.linkService = linkService;
        this.commonDao = commonDao;
        this.linkDao = linkDao;
        this.translationSynchronizer = translationSynchronizer;
    }

    @RequestMapping("for/{uri}")
    /**
     * Get all topics with any UID by his uri
     */
    public List<LinkedTopicPresentation> getForUri(@PathVariable String uri) throws Exception {
        // todo: move to TopicService and sort by link rate
        hasLength(uri);
        final List<Link> links = linkDao.getAllLinks(uri);
        List<LinkedTopicPresentation> presentations = new ArrayList<>();
        for (Link link : links) {
            Topic topic = null;
            if (link.getUid1() instanceof Topic) {
                topic = (Topic) link.getUid1();
            }
            if (link.getUid2() instanceof Topic) {
                topic = (Topic) link.getUid2();
            }
            if (topic != null)
                presentations.add(new LinkedTopicPresentation(topic, link.getRate(), link.getComment()));
        }
        Collections.sort(presentations, (o1, o2) -> o1.rate == null || o2.rate == null ? 0 : -o1.rate.compareTo(o2.rate));
        return presentations;
    }

    private class LinkedTopicPresentation {
        public String comment;
        public String uri;
        public String name;
        public Float rate;

        LinkedTopicPresentation(Topic topic, Float rate, String comment) {
            this.comment = comment;
            name = topic.getName();
            uri = topic.getUri();
            this.rate = rate;
        }
    }

    @RequestMapping(value = "import", method = POST)
    @Moderated(value = Action.TOPIC_CREATE, command = "@topicController.importTopics")
    public void importTopics(@RequestBody String topics) throws Exception {
        hasLength(topics);
        final Set<String> uniqueNames = new HashSet<>(Arrays.asList(topics.split("\n")));
        uniqueNames.stream()
                .filter(name -> !name.isEmpty())
                .forEachOrdered(name -> commonDao.save(new Topic(name)));
        topicService.reload();
    }

    @RequestMapping(value = "for", method = POST)
    @Moderated(value = Action.TOPIC_LINK_RESOURCE, command = "@topicController.addFor")
    public Topic addFor(@RequestParam String uri,
                        @RequestParam String name,
                        @RequestParam(required = false) String quote,
                        @RequestParam(required = false) String comment,
                        @RequestParam(required = false) Float rate) throws Exception {
        UID uid = commonDao.get(UriGenerator.getClassByUri(uri), uri);
        final TopicProvider topic = topicService.findOrCreate(name);
        topic.link(null, uid, comment, quote, rate);
        moderationService.notice(Action.TOPIC_RESOURCE_LINKED, topic.name(), uid.getUri());
        return topic.topic();
    }

    @RequestMapping(value = "for/items-range", method = POST)
    @Moderated(value = Action.TOPIC_LINK_RANGE, command = "@topicController.addFor")
    public void addFor(@RequestParam String from,
                       @RequestParam String to,
                       @RequestParam String topicName,
                       @RequestParam(required = false) String rangeName,
                       @RequestParam(required = false) String quote,
                       @RequestParam(required = false) String comment,
                       @RequestParam(required = false) Float rate) throws Exception {
        ItemsRange itemsRange = ItemsRange.builder().from(from).to(to).description(rangeName).build();
        final String itemsRangeUri = UriGenerator.generate(itemsRange);
        ItemsRange range = commonDao.get(ItemsRange.class, itemsRangeUri);
        if (range == null) {
            moderationService.check(Action.ITEMS_RANGE_CREATE);
            range = commonDao.save(itemsRange);
        } else {
            moderationService.check(Action.ITEMS_RANGE_UPDATE);
            range.setDescription(rangeName);
        }

        final TopicProvider topic = topicService.findOrCreate(topicName);
        topic.link(null, range, comment, quote, rate);
    }

    @RequestMapping(value = "update-rate", method = POST)
    @Moderated(value = Action.TOPIC_RESOURCE_LINK_RATE_UPDATE, command = "@topicController.updateRate")
    public void updateRate(@RequestParam String forUri, @RequestParam String name, @RequestParam Float rate) throws Exception {
        // fixme: для свеже созданой связи между видео и темой не возможно изменить рейт
        linkService.getByUris(forUri, UriGenerator.generate(Topic.class, name)).get().updater().rate(rate).commit();
    }

    @RequestMapping(value = "update-comment", method = POST)
    @Moderated(value = Action.TOPIC_RESOURCE_LINK_COMMENT_UPDATE, command = "@topicController.updateComment")
    public void updateComment(@RequestParam String forUri, @RequestParam String name, @RequestParam String comment) throws Exception {
        linkService.getByUris(forUri, UriGenerator.generate(Topic.class, name)).get().updater().comment(comment).commit();
    }

    @RequestMapping(value = "unlink-uri", method = POST)
    @Moderated(value = Action.TOPIC_UNLINK_RESOURCE, command = "@topicController.unlinkUri")
    public void unlinkUri(@RequestParam String uri, @RequestParam String topicUri) throws Exception {
        hasLength(uri);
        hasLength(topicUri);
        // todo: move this logic to topic service
        final List<Link> links = linkDao.getAllLinks(uri);
        for (Link link : links) {
            if ((link.getUid1() instanceof Topic && link.getUid1().getUri().equals(topicUri)) ||
                    (link.getUid2() instanceof Topic && link.getUid2().getUri().equals(topicUri))) {
                linkDao.remove(link.getLinkId());
                moderationService.notice(Action.TOPIC_RESOURCE_UNLINKED, topicUri, uri);
                return; // remove only first one
            }
        }
    }

    @RequestMapping("suggest")
    public Collection<String> suggest(@RequestParam String q) {
        return suggestionsController.suggestions(q, false, true, false, false, false, false, false, false, false, false).values();
    }

    @RequestMapping("add-child")
    @Moderated(value = Action.TOPIC_ADD_CHILD, command = "@topicController.addChild")
    public void addChild(@RequestParam String child, @RequestParam String name) {
        if (topicService.exist(name) && topicService.exist(child)) {
            boolean alreadyParent = topicService.getByName(child).children().anyMatch(c -> c.name().equals(name));
            if (alreadyParent) {
                throw new RuntimeException("The parent has a child for the given name");
            }
        }
        final TopicProvider parentTopic = topicService.findOrCreate(name);
        if (!parentTopic.getChild(child).isPresent()) {
            final TopicProvider childTopic = parentTopic.addChild(child);
            moderationService.notice(Action.TOPIC_CHILD_ADDED, parentTopic.name(), childTopic.name());
        }
    }

    @RequestMapping(value = "unlink", method = POST)
    @Moderated(value = Action.TOPIC_UNLINK_TOPIC, command = "@topicController.unlink")
    public void unlink(@RequestParam String name, @RequestParam String linked) {
        final TopicProvider unlinked = topicService.getByName(name).unlink(linked);
        if (unlinked != null) moderationService.notice(Action.TOPIC_TOPIC_UNLINKED, name, linked);
    }

    @RequestMapping("merge")
    // Слияние двух веток
    @Moderated(value = Action.TOPIC_MERGE, command = "@topicController.merge")
    public void merge(@RequestParam String main, @RequestParam String mergeInto) {
        topicService.getByName(main, true).merge(mergeInto);
    }

    /*@RequestMapping("add-related")
    public void addRelated(@RequestParam String name, @RequestParam String related) {
        topicService.findOrCreate(name).link(topicService.findOrCreate(related).topic());
    }*/

    @RequestMapping("children")
    public List<Topic> linkChild(@RequestParam String name) {
        return topicService.getByName(name)
                .children()
                .map(TopicProvider::topic).collect(toList());
    }

    @RequestMapping("parents")
    public List<Topic> linkParent(@RequestParam String name) {
        return topicService.getByName(name)
                .parents()
                .map(TopicProvider::topic).collect(toList());
    }


    @RequestMapping
    public GetTopicPresentation get(@RequestParam String name, @RequestParam(required = false) boolean includeResources) {
        TopicProvider topic = topicService.getByName(name);
        return GetTopicPresentation.builder()
                .name(topic.name())
                .uri(topic.uri())
                .children(topic.children().map(TopicProvider::name).collect(toList()))
                .parents(topic.parents().map(TopicProvider::name).collect(toList()))
                .related(topic.related().map(TopicProvider::name).collect(toList()))
                .resources(includeResources ? topic.resources() : null)
                .term(topic.linkedTerm().map(TermService.TermProvider::getName).orElse(null))
                .build();
    }

    @Builder
    private static class GetTopicPresentation {
        public String uri;
        public String name;
        public String term;
        public List<String> children;
        public List<String> parents;
        public List<String> related;
        public TopicProvider.TopicResources resources;
    }

    @RequestMapping("reload")
    public void reload() {
        topicService.reload();
    }

    @RequestMapping("bulk/link")
    //todo implement
    public void bulkLinkResources(String topicName, List<String> resourceUris) {
        // link each resource to topicName
        throw new RuntimeException("Unimplemented");
    }

    @RequestMapping("bulk/unlink")
    //todo implement
    public void bulkUnlinkResources(String topicName, List<String> resourceUris) {
        // unlink each resource from topicName
        throw new RuntimeException("Unimplemented");
    }

    @RequestMapping("last")
    public List<Topic> getLast(@PageableDefault @SortDefault(direction = Sort.Direction.DESC, sort = "createdAt") Pageable pageable) {
        return commonDao.getPage(Topic.class, pageable);
    }

    @RequestMapping(value = "sync-translation", method = GET)
    public ResponseEntity<?> syncTranslation() {
        translationSynchronizer.synchronize();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
