package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Topic;
import org.ayfaar.app.services.TopicService;
import org.ayfaar.app.utils.UriGenerator;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.util.Assert.hasLength;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("api/topic")
public class TopicController {

    @Inject
    CommonDao commonDao;
    @Inject
    LinkDao linkDao;
    @Inject
    TopicService topicService;

    GetTopicPresentation getTopicPresentation;

    @RequestMapping("for/{uri}")
    public List<TopicPresentation> getForUri(@PathVariable String uri) throws Exception {
        hasLength(uri);
        final List<Link> links = linkDao.getAllLinks(uri);
        List<TopicPresentation> topicPresentations = new ArrayList<>();
        for (Link link : links) {
            if (link.getUid1() instanceof Topic) {
                topicPresentations.add(new TopicPresentation((Topic) link.getUid1(), link.getRate()));
            }
            if (link.getUid2() instanceof Topic) {
                topicPresentations.add(new TopicPresentation((Topic) link.getUid2(), link.getRate()));
            }
        }
        return topicPresentations;
    }

    private class TopicPresentation {
        public String uri;
        public String name;
        public Float rate;

        TopicPresentation(Topic topic, Float rate) {
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
    }

    @RequestMapping(value = "for", method = POST)
    public Topic addFor(@RequestParam String uri, @RequestParam String name) throws Exception {
        hasLength(name);
        Topic topic = commonDao.get(Topic.class, "name", name);
        if (topic == null) topic = commonDao.save(new Topic(name));
        linkDao.save(new Link(topic, commonDao.get(UriGenerator.getClassByUri(uri), uri)));
        return topic;
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

    @RequestMapping("{name}/add-child/{child}")
    public void addChild(@PathVariable String name, @PathVariable String child) {
        topicService.getOrCreate(name).addChild(child);
    }

    @RequestMapping("{name}/children")
    public List<Topic> linkChild(@PathVariable String name) {
        return topicService.get(UriGenerator.generate(Topic.class, name))
                .orElseThrow(() -> new RuntimeException("Topic for " + name + " not found"))
                .children();
    }

    @RequestMapping("{name}/parents")
    public List<Topic> linkParent(@PathVariable String name) {
        return topicService.get(UriGenerator.generate(Topic.class, name))
                .orElseThrow(() -> new RuntimeException("Topic for " + name + " not found"))
                .parents();
    }


    @RequestMapping("{name}")
    public GetTopicPresentation get(@PathVariable String name) {
        try {

            getTopicPresentation.name = name;
            getTopicPresentation.uri = topicService.getOrCreate(name).uri();
            getTopicPresentation.children = topicService.getOrCreate(name).children().stream().map((topic) -> topic.getName()).collect(toList());
            getTopicPresentation.parents = topicService.getOrCreate(name).parents().stream().map((topic) -> topic.getName()).collect(toList());
            //getTopicPresentation.related = ?????

        }catch (Exception e){
            throw new RuntimeException("Unimplemented");
        }

        return getTopicPresentation;
    }

    private class GetTopicPresentation {
        public String uri;
        public String name;
        public List<String> children; // names of all children
        public List<String> parents; // names of all parents
        public List<String> related; // это те, у которых линки без укзания типа, то есть просто как-то связаны, не родительски и не дочерние

    }

}
