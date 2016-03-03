package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Topic;
import org.ayfaar.app.utils.Language;
import org.ayfaar.app.utils.UriGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.springframework.util.Assert.hasLength;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("api/topic")
public class TopicController {

    @Autowired CommonDao commonDao;
    @Autowired LinkDao linkDao;

    @RequestMapping("for/{uri}")
    @ResponseBody
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
    @ResponseBody
    public void importTopics(@RequestBody String topics) throws Exception {
        hasLength(topics);
        final Set<String> uniqueNames = new HashSet<>(Arrays.asList(topics.split("\n")));
        for (String name : uniqueNames) {
            if (!name.isEmpty())commonDao.save(new Topic(name, Language.ru));
        }
    }

    @RequestMapping(value = "for", method = POST)
    @ResponseBody
    public Topic addFor(@RequestParam String uri, @RequestParam String name) throws Exception {
        hasLength(name);
        Topic topic = commonDao.get(Topic.class, "name", name);
        if (topic == null) topic = commonDao.save(new Topic(name, Language.ru));
        linkDao.save(new Link(topic, commonDao.get(UriGenerator.getClassByUri(uri), uri)));
        return topic;
    }

    @RequestMapping(value = "rate", method = POST)
    @ResponseBody
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
    @ResponseBody
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
    @ResponseBody
    public List<String> suggest(@RequestParam String q) {
        List<Topic> topics = commonDao.getLike(Topic.class, "name", "%" + q + "%", 20);
        List<String> names = new ArrayList<String>();
        for (Topic topic : topics) {
            names.add(topic.getName());
        }
        return names;
    }
}
