package org.ayfaar.app.controllers;

import lombok.Builder;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Topic;
import org.ayfaar.app.model.UID;
import org.ayfaar.app.services.TopicProvider;
import org.ayfaar.app.services.TopicService;
import org.ayfaar.app.utils.UriGenerator;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.ayfaar.app.utils.StreamUtils.single;
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
        topicService.findOrCreate(name).addChild(child);
    }

    @RequestMapping("{name}/unlink/{linked}")
    // убрать связь
    // todo: Implement
    public void unlink(@PathVariable String name, @PathVariable String linked) {
        throw new RuntimeException("Unimplemented");
    }

    @RequestMapping("{name}/add-related/{related}")
    // todo: Implement
    public void addRelated(@PathVariable String name, @PathVariable String related) {
        throw new RuntimeException("Unimplemented");
    }

    @RequestMapping("{name}/children")
    public List<Topic> linkChild(@PathVariable String name) {
        return topicService.getByName(name)
                .children()
                .map(TopicProvider::topic).collect(toList());
    }

    @RequestMapping("{name}/parents")
    public List<Topic> linkParent(@PathVariable String name) {
        return topicService.getByName(name)
                .parents()
                .map(TopicProvider::topic).collect(toList());
    }


    @RequestMapping("{name}")
    public GetTopicPresentation get(@PathVariable String name) {
        TopicProvider topic = topicService.getByName(name);
        return GetTopicPresentation.builder()
                .name(topic.name())
                .uri(topic.uri())
                .children(topic.children().map(TopicProvider::name).collect(toList()))
                .parents(topic.parents().map(TopicProvider::name).collect(toList()))
                .related(topic.related().map(TopicProvider::name).collect(toList()))
                .build();
    }

    @Builder
    private static class GetTopicPresentation {
        public String uri;
        public String name;
        public List<String> children;
        public List<String> parents;
        public List<String> related;
    }

    // todo: метод для получения все ресурсов связанных с темой)
    // сделать доступной через url
    public ResourcesPresentation getResources(String name) {
        final Stream<TopicProvider.TopicResourcesGroup> resources = topicService.getByName(name).resources();
        // приобразовать полученые ресурсы к нужному виду
        return ResourcesPresentation.builder()
                .video(resources
                        .filter(group -> group.type.isVideo())
                        .map(group -> group.resources)
                        .collect(single()).get()
                )
                .build();
    }

    @Builder
    private static class ResourcesPresentation {
        public List<UID> video;
        // ещё будут позже
    }
}
