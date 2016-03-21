package org.ayfaar.app.controllers;

import lombok.Builder;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.model.ItemsRange;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Topic;
import org.ayfaar.app.model.UID;
import org.ayfaar.app.services.topics.TopicProvider;
import org.ayfaar.app.services.topics.TopicService;
import org.ayfaar.app.utils.UriGenerator;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.springframework.util.Assert.hasLength;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("api/topic")
public class TopicController {

    @Inject CommonDao commonDao;
    @Inject LinkDao linkDao;
    @Inject TopicService topicService;

    @RequestMapping("for/{uri}")
    public List<LinkedTopicPresentation> getForUri(@PathVariable String uri) throws Exception {
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
    public void importTopics(@RequestBody String topics) throws Exception {
        hasLength(topics);
        final Set<String> uniqueNames = new HashSet<>(Arrays.asList(topics.split("\n")));
        for (String name : uniqueNames) {
            if (!name.isEmpty()) commonDao.save(new Topic(name));
        }
        topicService.reload();
    }

    @RequestMapping(value = "for", method = POST)
    public Topic addFor(@RequestParam String uri,
                        @RequestParam String name,
                        @RequestParam(required = false) String quote,
                        @RequestParam(required = false) String comment,
                        @RequestParam(required = false) Float rate) throws Exception {
        UID uid = commonDao.get(UriGenerator.getClassByUri(uri), uri);
        final TopicProvider topic = topicService.findOrCreate(name);
        topic.link(null, uid, comment, quote, rate);
        return topic.topic();
    }

    @RequestMapping(value = "for/items-range", method = POST)
    public void addFor(@RequestParam String from,
                        @RequestParam String to,
                        @RequestParam String topicName,
                        @RequestParam(required = false) String rangeName,
                        @RequestParam(required = false) String quote,
                        @RequestParam(required = false) String comment,
                        @RequestParam(required = false) Float rate) throws Exception {
        ItemsRange itemsRange = ItemsRange.builder().from(from).to(to).description(rangeName).build();
        itemsRange = commonDao.save(itemsRange);
        final TopicProvider topic = topicService.findOrCreate(topicName);
        topic.link(null, itemsRange, comment, quote, rate);
    }

    @RequestMapping(value = "rate", method = POST)
    public void rate(@RequestParam String forUri, @RequestParam String topicUri, @RequestParam Float rate) throws Exception {
        final List<Link> links = linkDao.getAllLinks(forUri);
        for (Link link : links) {
            if ((link.getUid1() instanceof Topic && link.getUid1().getUri().equals(topicUri)) ||
                    (link.getUid2() instanceof Topic && link.getUid2().getUri().equals(topicUri))) {
                link.setRate(rate);
                linkDao.save(link);
            }
        }
    }

    @RequestMapping(value = "update-comment", method = POST)
    public void updateComment(@RequestParam String forUri, @RequestParam String name, @RequestParam String comment) throws Exception {
        final List<Link> links = linkDao.getAllLinks(forUri);
        for (Link link : links) {
            if ((link.getUid1() instanceof Topic && ((Topic) link.getUid1()).getName().equals(name)) ||
                    (link.getUid2() instanceof Topic && ((Topic) link.getUid2()).getName().equals(name))) {
                link.setComment(comment);
                linkDao.save(link);
            }
        }
    }

    @RequestMapping(value = "for", method = DELETE)
    public void deleteFor(@RequestParam String uri, @RequestParam String topicUri) throws Exception {
        hasLength(uri);
        hasLength(topicUri);
        final List<Link> links = linkDao.getAllLinks(uri);
        for (Link link : links) {
            if ((link.getUid1() instanceof Topic && link.getUid1().getUri().equals(topicUri)) ||
                    (link.getUid2() instanceof Topic && link.getUid2().getUri().equals(topicUri))) {
                linkDao.remove(link.getLinkId());
                return; // remove only first one
            }
        }
    }

    @RequestMapping("suggest")
    public List<String> suggest(@RequestParam String q) {
        List<Topic> topics = commonDao.getLike(Topic.class, "name", "%" + q + "%", 20);
        List<String> names = new ArrayList<String>();
        for (Topic topic : topics) {
            names.add(topic.getName());
        }
        return names;
    }

    @RequestMapping("add-child")
    public void addChild(@RequestParam String name, @RequestParam String child) {
        if (topicService.hasTopic(name) && topicService.hasTopic(child)) {
            boolean alreadyParent = topicService.getByName(child).children().anyMatch(c -> c.name().equals(name));
            if (alreadyParent) {
                throw new RuntimeException("The parent has a child for the given name");
            }
        }
        topicService.findOrCreate(name).addChild(child);
    }

    @RequestMapping("unlink")
    public void unlink(@RequestParam String name,@RequestParam String linked) {
        topicService.getByName(name).unlink(linked);
    }

    @RequestMapping("merge")
    // Слияние двух веток
    public void merge(@RequestParam String main, @RequestParam String mergeInto) {
        topicService.getByName(main).merge(mergeInto).delete();
    }

    @RequestMapping("add-related")
    public void addRelated(@RequestParam String name, @RequestParam String related) {
        topicService.findOrCreate(name).link(topicService.findOrCreate(related).topic());
    }

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
                .build();
    }

    @Builder
    private static class GetTopicPresentation {
        public String uri;
        public String name;
        public List<String> children;
        public List<String> parents;
        public List<String> related;
        public TopicProvider.TopicResources resources;
    }
}
